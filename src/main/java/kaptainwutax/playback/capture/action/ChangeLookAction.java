package kaptainwutax.playback.capture.action;

public class ChangeLookAction extends AbstractAction {

	private double cursorDeltaX;
	private double cursorDeltaY;

	public ChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.cursorDeltaX = cursorDeltaX;
		this.cursorDeltaY = cursorDeltaY;
	}

	@Override
	public void play() {
		client.player.changeLookDirection(this.cursorDeltaX, this.cursorDeltaY);
	}

}
