package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.ChangeLookAction;
import kaptainwutax.playback.capture.action.InputAction;
import kaptainwutax.playback.capture.action.KeyBindingAction;
import net.minecraft.client.input.Input;
import net.minecraft.client.options.KeyBinding;

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

	public void addKeyBindingAction(KeyBinding key, boolean state) {
		KeyBindingAction action = new KeyBindingAction(key, state);
		if(action.isValid())this.addAction(action);
	}

}
