package kaptainwutax.playback.capture;

import net.minecraft.client.input.Input;
import net.minecraft.network.Packet;

public class TickCaptures {

	public static final TickCaptures EMPTY = new TickCaptures();

	public FirstPersonTickCapture first = new FirstPersonTickCapture();
	public ThirdPersonTickCapture third = new ThirdPersonTickCapture();

	public void play(ReplayView view) {
		if(view == ReplayView.FIRST_PERSON)this.first.play();
		else if(view == ReplayView.THIRD_PERSON)this.third.play();
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

	public void recordInputAction(Input input) {
		this.third.addInputAction(input);
	}

	public void recordChangeLook(double cursorDeltaX, double cursorDeltaY) {
		this.third.addChangeLookAction(cursorDeltaX, cursorDeltaY);
	}

	public void recordSprint(boolean pressed) {
		this.third.addSprintAction(pressed);
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
