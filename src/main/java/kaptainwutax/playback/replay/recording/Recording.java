package kaptainwutax.playback.replay.recording;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.action.ExtraStateAction;
import kaptainwutax.playback.replay.capture.TickInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleConsumer;

public class Recording implements AutoCloseable {

	//TODO: Save this!
	protected ExtraStateAction extraStateAction;
	protected Long2ObjectMap<TickInfo> recording = new Long2ObjectOpenHashMap<>();

	protected final File file;
	protected final RandomAccessFile randomAccessFile;
	/**
	 * file offset where the next tick is written
	 */
	private long fileOffset;
	private long lastTick;

	public long currentTick = 0;
	private TickInfo currentTickInfo = new TickInfo(this);

	public static final ThreadLocal<Recording> currentlyReading = new ThreadLocal<>();

	public Recording() {
		this.file = null;
		this.randomAccessFile = null;
	}

	public Recording(File file, String mode) throws FileNotFoundException {
		this.file = file;
		this.randomAccessFile = new RandomAccessFile(file, mode);
	}

	public ExtraStateAction getExtraStateAction() {
		return this.extraStateAction;
	}

	public void recordJoinPacket(Packet<ClientPlayPacketListener> packet) {
		this.extraStateAction.addJoinPacket(packet);
	}

	public void recordPerspective(int perspective) {
		this.extraStateAction.addPerspective(perspective);
	}

	public void recordPhysicalSide(boolean isSinglePlayer) {
		this.extraStateAction.addPhysicalSide(isSinglePlayer);
	}

	public void tickRecord(long tick) {
		if(tick == this.currentTick) return;

		if(!this.currentTickInfo.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickInfo);
		}

		System.out.println("Tick " + tick);
		if (randomAccessFile != null && !currentTickInfo.isEmpty()) {
			System.out.println("Writing to " + file);
			try {
				recordToFile(tick, currentTickInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (randomAccessFile == null) {
			System.out.println("Not writing: no file");
		}

		this.currentTickInfo = new TickInfo(this);
		this.currentTick = tick;
		lastTick = tick;
	}

	private void recordToFile(long tick, TickInfo currentTickInfo) throws IOException {
		writeHeader(tick);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarLong(tick);
		currentTickInfo.write(buf);
		randomAccessFile.writeInt(buf.readableBytes());
		while (buf.isReadable()) {
			buf.readBytes(randomAccessFile.getChannel(), buf.readableBytes());
		}
		buf.release();
		fileOffset = randomAccessFile.getFilePointer();
	}

	private void writeHeader(long tick) throws IOException {
		randomAccessFile.seek(0);
		randomAccessFile.writeLong(tick);
		if (fileOffset == 0) {
			fileOffset = randomAccessFile.getFilePointer();
		} else {
			randomAccessFile.seek(fileOffset);
		}
	}

	private long readTickFromFile() throws IOException {
		int size = randomAccessFile.readInt();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(size));
		while (size > 0) {
			int read = buf.writeBytes(randomAccessFile.getChannel(), size);
			if (read < 0) throw new EOFException();
			size -= read;
		}
		long tick = buf.readVarLong();
		TickInfo info = new TickInfo(this);
		info.read(buf);
		recording.put(tick, info);
		return tick;
	}

	public void loadHeader() throws IOException {
		randomAccessFile.seek(0);
		lastTick = randomAccessFile.readLong();
		if (fileOffset == 0) {
			fileOffset = randomAccessFile.getFilePointer();
		} else {
			randomAccessFile.seek(fileOffset);
		}
	}

	public RecordingSummary readSummary() throws IOException {
		if (randomAccessFile == null) return new RecordingSummary(null, 0, lastTick);
		loadHeader();
		return new RecordingSummary(file, randomAccessFile.length(), lastTick);
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
		return this.extraStateAction.isSinglePlayer();
	}

	@Override
	public void close() throws IOException {
		if (randomAccessFile != null) randomAccessFile.close();
	}

}
