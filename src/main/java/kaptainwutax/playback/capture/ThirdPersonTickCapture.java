package kaptainwutax.playback.capture;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.action.ChangeLookAction;
import kaptainwutax.playback.capture.action.KeyBindingAction;
import kaptainwutax.playback.capture.action.ScrollInHotbarAction;
import kaptainwutax.playback.capture.action.SetFlySpeedAction;

public class ThirdPersonTickCapture extends CommonTickCapture {

	private KeyBindingAction keyAction;

	public ThirdPersonTickCapture() {

	}

	public KeyBindingAction getKeyAction() {
		if(this.keyAction == null) {
			this.keyAction = new KeyBindingAction();
		}

		return this.keyAction;
	}

	public void setKeyAction(KeyBindingAction keyAction) {
		this.keyAction = keyAction;
	}

	public void addChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.addAction(new ChangeLookAction(cursorDeltaX, cursorDeltaY));
	}

	public void addScrollInHotbarAction(double scrollAmount) {
		this.addAction(new ScrollInHotbarAction(scrollAmount));
	}

	public void addSetFlySpeedAction(float flySpeed) {
		this.addAction(new SetFlySpeedAction(flySpeed));
	}

	@Override
	public void play() {
		super.play();

		if(this.keyAction != null) {
			this.keyAction.play();
		}
	}

}
