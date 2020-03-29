package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public class TickInfo {

	public static final TickInfo EMPTY = new TickInfo();

	public TickCapture tickCapture = new TickCapture();

	public void play(ReplayView view) {
		Playback.isProcessingReplay = true;
		Playback.allowInput = true;
		//if(view == ReplayView.FIRST_PERSON)
		this.tickCapture.play();
		//else if(view == ReplayView.THIRD_PERSON) this.third.play();
		Playback.isProcessingReplay = false;
		Playback.allowInput = Playback.allowInputDefault;
	}

	public void recordPacket(Packet<?> packet) {
		if(packet instanceof GameJoinS2CPacket) {
			Playback.recording.joinPacket = new PacketAction(packet);
			return;
		}

		this.tickCapture.addPacketAction(packet);
	}

	public void recordKey(int action, int key, int scanCode, int i, int j) {
		this.tickCapture.addKeyAction(action, key, scanCode, i, j);
	}

	public void recordMouse(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.tickCapture.addMouseAction(action, d1, d2, i1, isCursorLocked);
	}

	public void recordKeyState(long handle, int i) {
		this.tickCapture.addKeyState(handle, i);
	}

	public boolean getKeyState(long handle, int i) {
		return this.tickCapture.getKeyState(handle, i);
	}

	public void recordFirstTickFixes() {
		this.tickCapture.addF5ModeFixAction(MinecraftClient.getInstance().options.perspective);
	}

	public void recordDebug() {
		this.tickCapture.addDebugAction(new DebugAction());
	}

	public boolean isEmpty() {
		return this.tickCapture.isEmpty();
	}

}
