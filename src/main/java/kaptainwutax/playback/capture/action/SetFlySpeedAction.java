package kaptainwutax.playback.capture.action;

public class SetFlySpeedAction extends AbstractAction {

	private float flySpeed;

	public SetFlySpeedAction(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	@Override
	public void play() {
		client.player.abilities.setFlySpeed(this.flySpeed);
	}

}
