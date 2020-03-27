package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.replay.capture.FirstPersonTickCapture;
import kaptainwutax.playback.replay.capture.ThirdPersonTickCapture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public class TickInfo {

	public static final TickInfo EMPTY = new TickInfo();

	public FirstPersonTickCapture first = new FirstPersonTickCapture();
	public ThirdPersonTickCapture third = new ThirdPersonTickCapture();

	public void play(ReplayView view) {
		Playback.isProcessingReplay = true;
		Playback.allowInput = true;
		if(view == ReplayView.FIRST_PERSON) this.first.play();
		else if(view == ReplayView.THIRD_PERSON) this.third.play();
		Playback.isProcessingReplay = false;
		Playback.allowInput = Playback.allowInputDefault;
	}

	public void recordPacket(Packet<?> packet) {
		if(packet instanceof GameJoinS2CPacket) {
			Playback.recording.joinPacket = new PacketAction(packet);
			return;
		}

		this.first.addPacketAction(packet);
		this.third.addPacketAction(packet);
	}

	public void recordKey(int action, int key, int scanCode, int i, int j) {
		this.first.addKeyAction(action, key, scanCode, i, j);
	}

	public void recordMouse(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.first.addMouseAction(action, d1, d2, i1, isCursorLocked);
	}

	public void recordKeyState(long handle, int i) {
		this.first.addKeyState(handle, i);
	}

	public boolean getKeyState(long handle, int i) {
		return this.first.getKeyState(handle, i);
	}

	public void recordChangeLook(double cursorDeltaX, double cursorDeltaY) {
		this.third.addChangeLookAction(cursorDeltaX, cursorDeltaY);
	}

	public void recordScrollInHotbar(double scrollAmount) {
		this.third.addScrollInHotbarAction(scrollAmount);
	}

	public void recordSetFlySpeed(float flySpeed) {
		this.third.addSetFlySpeedAction(flySpeed);
	}

	public void recordFirstTickFixes() {
		this.first.addF5ModeFixAction(MinecraftClient.getInstance().options.perspective);
	}

	public void recordDebug() {
		this.first.addDebugAction(new DebugAction());
		this.third.addDebugAction(new DebugAction());
	}

	public boolean isEmpty() {
		return this.first.isEmpty() && this.third.isEmpty();
	}

}
