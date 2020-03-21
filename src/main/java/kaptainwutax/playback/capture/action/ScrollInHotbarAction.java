package kaptainwutax.playback.capture.action;

import net.minecraft.client.MinecraftClient;

public class ScrollInHotbarAction implements IAction {

	private double scrollAmount;

	public ScrollInHotbarAction(double scrollAmount) {
		this.scrollAmount = scrollAmount;
	}

	@Override
	public void play() {
		MinecraftClient.getInstance().player.inventory.scrollInHotbar(this.scrollAmount);
	}

}
