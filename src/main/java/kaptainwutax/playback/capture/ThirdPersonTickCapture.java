package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.ChangeLookAction;
import kaptainwutax.playback.capture.action.KeyBindingAction;
import net.minecraft.client.options.KeyBinding;

public class ThirdPersonTickCapture extends CommonTickCapture {

	public ThirdPersonTickCapture() {

	}

	public void addChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.addAction(new ChangeLookAction(cursorDeltaX, cursorDeltaY));
	}

	public void addKeyBindingAction(int action, KeyBinding key, boolean state) {
		KeyBindingAction keyBindingAction = new KeyBindingAction(action, key, state);
		if(keyBindingAction.isValid())this.addAction(keyBindingAction);
	}

}
