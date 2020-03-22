package kaptainwutax.playback.capture.action;

import net.minecraft.client.MinecraftClient;

public class ChangeLookAction implements IAction {

	private double cursorDeltaX;
	private double cursorDeltaY;

	public ChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.cursorDeltaX = cursorDeltaX;
		this.cursorDeltaY = cursorDeltaY;
	}

	@Override
	public void play() {
		MinecraftClient.getInstance().player.changeLookDirection(this.cursorDeltaX, this.cursorDeltaY);
	}

}
