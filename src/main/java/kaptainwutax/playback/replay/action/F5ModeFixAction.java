package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.replay.action.Action;
import net.minecraft.client.MinecraftClient;

public class F5ModeFixAction extends Action {

	private int perspectiveF5mode;

	public F5ModeFixAction(int perspectiveF5mode) {
		this.perspectiveF5mode = perspectiveF5mode;
	}

	@Override
	public void play() {
		MinecraftClient.getInstance().options.perspective = this.perspectiveF5mode;
	}

}