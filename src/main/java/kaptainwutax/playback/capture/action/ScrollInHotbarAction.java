package kaptainwutax.playback.capture.action;

public class ScrollInHotbarAction extends Action {

	private double scrollAmount;

	public ScrollInHotbarAction(double scrollAmount) {
		this.scrollAmount = scrollAmount;
	}

	@Override
	public void play() {
		client.player.inventory.scrollInHotbar(this.scrollAmount);
	}

}
