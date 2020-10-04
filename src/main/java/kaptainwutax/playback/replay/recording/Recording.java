package kaptainwutax.playback.replay.recording;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import kaptainwutax.playback.replay.capture.StartState;
import kaptainwutax.playback.replay.capture.TickInfo;
import kaptainwutax.playback.util.SerializationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Perspective;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleConsumer;

public class Recording implements AutoCloseable {
	public static final int FORMAT_VERSION = 15;
	private static final int HEADER_SIZE = 16;

	protected StartState startState = new StartState();
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

	transient protected Set<Integer> currentKeyStates = new HashSet<>();
	transient protected WindowSize currentRecordedWindowSize;
	transient protected String clipboard;
	transient protected boolean paused;


	public static final ThreadLocal<Recording> currentlyReading = new ThreadLocal<>();

	public Recording(File file, String mode) throws FileNotFoundException {
		this.file = file;
		this.randomAccessFile = new RandomAccessFile(file, mode);
	}

	public StartState getStartState() {
		return this.startState;
	}

	public void recordJoinPacket(Packet<ClientPlayPacketListener> packet) {
		this.startState.addJoinPacket(packet);
	}

	public void recordPerspective(Perspective perspective) {
		this.startState.addPerspective(perspective);
	}

	public void recordPhysicalSide(boolean isSinglePlayer) {
		this.startState.addPhysicalSide(isSinglePlayer);
	}

	public void recordInitialWindowFocus(boolean windowFocus) {
		this.startState.addWindowFocus(windowFocus);
	}

	public void recordInitialWindowSize(WindowSize windowSize) {
		this.startState.addWindowSize(windowSize);
	}

	public void recordGameOptions(GameOptions options) {
		this.startState.addGameOptions(options);
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
		startState.write(buf);
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
			startState.read(buf);
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
		loadHeader();
		return new RecordingSummary(file, version, randomAccessFile.length(), lastTick, startState);
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
		this.currentTickInfo.playTick();
	}

	public void playFrame(long tick, float tickDelta) {
		this.currentTick = tick;
		this.currentTickInfo = this.recording.getOrDefault(tick, TickInfo.EMPTY);
		this.currentTickInfo.playFrame(tickDelta);
	}

	public TickInfo getCurrentTickInfo() {
		return this.currentTickInfo;
	}

	public long getEnd() {
		return lastTick;
	}

	public boolean isSinglePlayerRecording() {
		return this.startState.isSinglePlayer();
	}

	@Override
	public void close() throws IOException {
		if (randomAccessFile != null) randomAccessFile.close();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
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


	public String getClipboardNow() {
		return this.clipboard == null ? "" : this.clipboard;
	}

	public void setClipboard(String clipboard) {
		this.clipboard = clipboard == null ? "" : clipboard;
	}

	public WindowSize getCurrentRecordedWindowSize() {
		return currentRecordedWindowSize;
	}

	public void setCurrentRecordedWindowSize(WindowSize windowSize) {
		this.currentRecordedWindowSize = windowSize;
	}

	public boolean isTickPaused() {
		return this.paused;
	}

	public void playUpTo(long tickCounter, int tick) {
		if(tick > tickCounter) {
			if(Playback.getManager().isPaused()) {
				Playback.getManager().togglePause();
			}

			for(; tickCounter < tick; tickCounter++) {
				MinecraftClient.getInstance().tick();
			}

			Playback.getManager().togglePause();
		}
	}

}
