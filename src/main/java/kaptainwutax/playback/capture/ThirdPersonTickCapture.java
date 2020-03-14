package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.ChangeLookAction;
import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.InputAction;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ThirdPersonTickCapture extends CommonTickCapture {

	public InputAction input;
	public boolean isSprinting;

	public ThirdPersonTickCapture() {

	}

	public void addInputAction(Input input) {
		this.input = new InputAction(input);
	}

	public void addChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.addAction(new ChangeLookAction(cursorDeltaX, cursorDeltaY));
	}

	public void addSprintAction(boolean pressed) {
		this.isSprinting = pressed;
	}

}
