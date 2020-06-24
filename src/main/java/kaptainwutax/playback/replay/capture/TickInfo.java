package kaptainwutax.playback.replay.capture;

import io.netty.buffer.Unpooled;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.action.KeyAction;
import kaptainwutax.playback.replay.action.MouseAction;
import kaptainwutax.playback.replay.recording.Recording;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class TickInfo implements PlaybackSerializable {

	public static final TickInfo EMPTY = new TickInfo(null);

	public TickCapture tickCapture = new TickCapture();
	private final Recording recording;

	public TickInfo(Recording recording) {
		this.recording = recording;
	}

	public void playTick() {
		Playback.getManager().isProcessingReplay = true;
		this.tickCapture.finishTick();
		Playback.getManager().isProcessingReplay = false;
	}

	public void playFrame(float tickDelta) {
		Playback.getManager().isProcessingReplay = true;
		this.tickCapture.playFrame(tickDelta);
		Playback.getManager().isProcessingReplay = false;
	}

	public void recordPacket(Packet<ClientPlayPacketListener> packet) {
		if(packet instanceof GameJoinS2CPacket) {
			return;
		}

		this.tickCapture.addPacketAction(packet);
	}

	public void recordKey(KeyAction.ActionType action, int key, int scanCode, int i, int j) {
		this.tickCapture.addKeyAction(action, key, scanCode, i, j);

		if(i != GLFW.GLFW_REPEAT) {
			Playback.getManager().recording.setKeyState(key, i == GLFW.GLFW_PRESS);
		}
	}

	public MouseAction recordMouse(MouseAction.ActionType action, double d1, double d2, int i1) {
		return this.tickCapture.addMouseAction(action, d1, d2, i1);
	}

    public void recordWindowFocus(boolean windowFocus) {
        this.tickCapture.addWindowFocusAction(windowFocus);
    }

    public void recordWindowSize(WindowSize windowSize, boolean runOnResolutionChanged) {this.tickCapture.addWindowSizeAction(windowSize, runOnResolutionChanged);}

	public void recordDebug() {
		this.tickCapture.addDebugAction(new DebugAction(MinecraftClient.getInstance().player));
	}

	public boolean isEmpty() {
		return this.tickCapture.isEmpty();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int captureSize = buf.readVarInt();
		PacketByteBuf sliced = new PacketByteBuf(buf.slice(buf.readerIndex(), captureSize));
		this.tickCapture.read(sliced);
		if (sliced.readerIndex() != captureSize)
			throw new IndexOutOfBoundsException();
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

	public void recordClipboard(String clipboard) {
	    if (clipboard == null) clipboard = "";
		if (clipboard.length() > 32767) {
			System.out.println("Only recorded first 32767 characters of the clipboard when pasting because it was too long ("+ clipboard.length() +")!");
			clipboard = clipboard.substring(0, 32767);
		}

		if (!this.recording.getClipboardNow().equals(clipboard)) {
			this.recording.setClipboard(clipboard);
			this.tickCapture.addClipboardReadAction(clipboard);
		}

	}

	public void recordLostFocusPause() {
		this.tickCapture.addLostFocusPauseAction();
	}

	public void recordPauseChanged(boolean paused) {
		this.tickCapture.addPauseChangedAction(paused);
	}
}
