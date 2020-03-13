package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;

public class ChangeLookAction implements IAction {

	private double cursorDeltaX;
	private double cursorDeltaY;

	public ChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.cursorDeltaX = cursorDeltaX;
		this.cursorDeltaY = cursorDeltaY;
	}

	@Override
	public void play() {
		Playback.manager.replayPlayer.getPlayer().changeLookDirection(this.cursorDeltaX, this.cursorDeltaY);
	}

}
