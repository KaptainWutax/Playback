package kaptainwutax.playback.replay.recording;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.action.StartStateAction;
import kaptainwutax.playback.replay.capture.TickInfo;
import kaptainwutax.playback.util.SerializationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleConsumer;

public class Recording implements AutoCloseable {
	public static int FORMAT_VERSION = 4;
	private static int HEADER_SIZE = 16;

	protected StartStateAction startStateAction = new StartStateAction();
	protected Long2ObjectMap<TickInfo> recording = new Long2ObjectOpenHashMap<>();

	protected final File file;
	protected final RandomAccessFile randomAccessFile;
	/**
	 * file offset where the next tick is written
	 */
	private long fileOffset = HEADER_SIZE;
	private long lastTick;
	private boolean startStateWritten;
	private int version = FORMAT_VERSION;

	public long currentTick = 0;
	private TickInfo currentTickInfo = new TickInfo(this);

	transient protected Set<Integer> currentKeyStates = new HashSet<>(); //todo clear this at recording start etc


	public static final ThreadLocal<Recording> currentlyReading = new ThreadLocal<>();

	public Recording() {
		this.file = null;
		this.randomAccessFile = null;
	}

	public Recording(File file, String mode) throws FileNotFoundException {
		this.file = file;
		this.randomAccessFile = new RandomAccessFile(file, mode);
	}

	public StartStateAction getStartStateAction() {
		return this.startStateAction;
	}

	public void recordJoinPacket(Packet<ClientPlayPacketListener> packet) {
		this.startStateAction.addJoinPacket(packet);
	}

	public void recordPerspective(int perspective) {
		this.startStateAction.addPerspective(perspective);
	}

	public void recordPhysicalSide(boolean isSinglePlayer) {
		this.startStateAction.addPhysicalSide(isSinglePlayer);
	}

	public void recordInitialWindowFocus(boolean windowFocus) {
		this.startStateAction.addWindowFocus(windowFocus);
	}

	public void tickRecord(long tick) {
		if(tick == this.currentTick) return;

		if(!this.currentTickInfo.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickInfo);
		}

		//System.out.println("Tick " + tick);
		if (randomAccessFile != null && !currentTickInfo.isEmpty()) {
			try {
				recordToFile(tick, currentTickInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.currentTickInfo = new TickInfo(this);
		this.currentTick = tick;
		lastTick = tick;
	}

	private void recordToFile(long tick, TickInfo currentTickInfo) throws IOException {
		if (!startStateWritten) {
			writeStartState();
		} else {
			writeHeader(tick);
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarLong(tick);
		currentTickInfo.write(buf);
		SerializationUtil.writeSizedBuffer(buf, randomAccessFile);
		fileOffset = randomAccessFile.getFilePointer();
	}

	private void writeHeader(long tick) throws IOException {
		randomAccessFile.seek(4);
		randomAccessFile.writeLong(tick);
		randomAccessFile.seek(fileOffset);
	}

	private void writeStartState() throws IOException {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		startStateAction.write(buf);
		randomAccessFile.seek(0);
		randomAccessFile.writeInt(version);
		randomAccessFile.seek(12);
		SerializationUtil.writeSizedBuffer(buf, randomAccessFile);
		fileOffset = randomAccessFile.getFilePointer();
		startStateWritten = true;
	}

	private long readTickFromFile() throws IOException {
		long offset = randomAccessFile.getFilePointer();
		try {
			PacketByteBuf buf = SerializationUtil.readSizedBuffer(randomAccessFile);
			long tick = buf.readVarLong();
			TickInfo info = new TickInfo(this);
			info.read(buf);
			buf.release();
			recording.put(tick, info);
			return tick;
		} catch (Exception e) {
			throw new IOException("Error reading tick from file @" + offset + "-" + randomAccessFile.getFilePointer(), e);
		}
	}

	public void loadHeader() throws IOException {
		try {
			randomAccessFile.seek(0);
			version = randomAccessFile.readInt();
			if (version != FORMAT_VERSION) {
				System.out.println("Not loading header of unknown version " + version);
				return;
			}
			lastTick = randomAccessFile.readLong();
			PacketByteBuf buf = SerializationUtil.readSizedBuffer(randomAccessFile);
			startStateAction.read(buf);
			buf.release();
			if (fileOffset == 0) {
				fileOffset = randomAccessFile.getFilePointer();
			} else {
				randomAccessFile.seek(fileOffset);
			}
		} catch (Exception e) {
			throw new IOException("Error reading header", e);
		}
	}

	public RecordingSummary readSummary() throws IOException {
		if (randomAccessFile == null) return new RecordingSummary(null, version, 0, lastTick, startStateAction);
		loadHeader();
		return new RecordingSummary(file, version, randomAccessFile.length(), lastTick, startStateAction);
	}

	public CompletableFuture<Void> loadAsync(DoubleConsumer progressListener) {
		return CompletableFuture.runAsync(() -> {
			try {
				load(progressListener);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	public void load(DoubleConsumer progressListener) throws IOException {
		currentlyReading.set(this);
		fileOffset = 0;
		loadHeader();
		long previousTick = 0;
		while (previousTick < lastTick) {
			previousTick = readTickFromFile();
			if (progressListener != null) {
				double progress = (double)(previousTick + 1) / (double)(lastTick + 1);
				progressListener.accept(progress);
			}
		}
		currentlyReading.set(null);
	}

	public void playTick(long tick) {
		this.currentTick = tick;
		this.currentTickInfo = this.recording.getOrDefault(tick, TickInfo.EMPTY);
		this.currentTickInfo.play(Playback.getManager().getView());
	}

	public TickInfo getCurrentTickInfo() {
		return this.currentTickInfo;
	}

	public long getEnd() {
		return lastTick;
	}

	public boolean isSinglePlayerRecording() {
		return this.startStateAction.isSinglePlayer();
	}

	@Override
	public void close() throws IOException {
		if (randomAccessFile != null) randomAccessFile.close();
	}

	public void setKeyState(int key, boolean pressed) {
		if (pressed) {
			this.currentKeyStates.add(key);
		} else {
			this.currentKeyStates.remove(key);
		}
	}

	public boolean getKeyState(int key) {
		return this.currentKeyStates.contains(key);
	}

}
