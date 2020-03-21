package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.DebugRotationAction;
import kaptainwutax.playback.capture.action.DebugSneakingAction;
import kaptainwutax.playback.capture.action.DebugVelocityAction;
import net.minecraft.client.input.Input;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.Packet;

public class TickCaptures {

	public static final TickCaptures EMPTY = new TickCaptures();

	public FirstPersonTickCapture first = new FirstPersonTickCapture();
	public ThirdPersonTickCapture third = new ThirdPersonTickCapture();

	public void play(ReplayView view) {
		//Playback.manager.replayPlayer.apply();
		if(view == ReplayView.FIRST_PERSON) this.first.play();
		else if(view == ReplayView.THIRD_PERSON) this.third.play();
		//Playback.manager.updateView(Playback.manager.getView());
	}

	public void recordPacket(Packet<?> packet) {
		this.first.addPacketAction(packet);
		this.third.addPacketAction(packet);
	}

	public void recordKey(int action, long window, int key, int scanCode, int i, int j) {
		this.first.addKeyAction(action, window, key, scanCode, i, j);
	}

	public void recordMouse(int action, long window, double d1, double d2, int i1) {
		this.first.addMouseAction(action, window, d1, d2, i1);
	}

	public void recordChangeLook(double cursorDeltaX, double cursorDeltaY) {
		this.third.addChangeLookAction(cursorDeltaX, cursorDeltaY);
	}

	public void recordDebugPosition(double x, double y, double z) {
		DebugPositionAction debPosAction = new DebugPositionAction(x,y,z);
		this.first.addDebugAction(debPosAction);
		this.third.addDebugAction(debPosAction);
	}

	public void recordDebugVelocity(double x, double y, double z) {
		DebugVelocityAction debVelAction = new DebugVelocityAction(x,y,z);
		this.first.addDebugAction(debVelAction);
		this.third.addDebugAction(debVelAction);
	}

	public void recordDebugRotation(float pitch, float yaw){
		DebugRotationAction debRotAction = new DebugRotationAction(pitch, yaw);
		this.first.addDebugAction(debRotAction);
		this.third.addDebugAction(debRotAction);
	}

	public void recordDebugSneaking(boolean state, EntityPose pose) {
		DebugSneakingAction debSneakAction = new DebugSneakingAction(state, pose);
		this.first.addDebugAction(debSneakAction);
		this.third.addDebugAction(debSneakAction);
	}

	public void recordKeyState(long handle, int i) {
		this.first.addKeyState(handle, i);
	}

	public boolean getKeyState(long handle, int i) {
		return this.first.getKeyState(handle, i);
	}

	public boolean isEmpty() {
		return this.first.isEmpty() && this.third.isEmpty();
	}

}
