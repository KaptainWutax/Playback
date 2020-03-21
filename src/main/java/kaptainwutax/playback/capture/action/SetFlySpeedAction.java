package kaptainwutax.playback.capture.action;

import net.minecraft.client.MinecraftClient;

public class SetFlySpeedAction implements IAction {

	private float flySpeed;

	public SetFlySpeedAction(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	@Override
	public void play() {
		MinecraftClient.getInstance().player.abilities.setFlySpeed(this.flySpeed);
	}

}
