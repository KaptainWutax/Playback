package kaptainwutax.playback.replay.capture;

import io.netty.buffer.Unpooled;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.recording.Recording;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class TickInfo implements PlaybackSerializable {

	public static final TickInfo EMPTY = new TickInfo(null);

	public TickCapture tickCapture = new TickCapture();
	private final Recording recording;

	public TickInfo(Recording recording) {
		this.recording = recording;
	}

	public void play(ReplayView view) {
		Playback.isProcessingReplay = true;
		this.tickCapture.play();
		Playback.isProcessingReplay = false;
	}

	public void recordPacket(Packet<ClientPlayPacketListener> packet) {
		if(packet instanceof GameJoinS2CPacket) {
			this.recording.getExtraStateAction().addJoinPacket(packet);
			return;
		}

		this.tickCapture.addPacketAction(packet);
	}

	public void recordPerspective(int perspective) {
		this.recording.getExtraStateAction().addPerspective(perspective);
	}

	public void recordKey(int action, int key, int scanCode, int i, int j) {
		this.tickCapture.addKeyAction(action, key, scanCode, i, j);
	}

	public void recordMouse(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.tickCapture.addMouseAction(action, d1, d2, i1, isCursorLocked);
	}

	public void recordKeyState(int i) {
		this.tickCapture.addKeyState(i);
	}

	public boolean getKeyState(int i) {
		return this.tickCapture.getKeyState(i);
	}

	public void recordDebug() {
		this.tickCapture.addDebugAction(new DebugAction(MinecraftClient.getInstance().player));
	}

	public boolean isEmpty() {
		return this.tickCapture.isEmpty();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int captureSize = buf.readVarInt();
		this.tickCapture.read(new PacketByteBuf(buf.slice(buf.readerIndex(), captureSize)));
		buf.readerIndex(buf.readerIndex() + captureSize);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
		tickCapture.write(tmp);
		buf.writeVarInt(tmp.writerIndex());
		buf.writeBytes(tmp);
		tmp.release();
	}

}
