package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;

public class LostFocusPauseScreenAction extends Action {

	public LostFocusPauseScreenAction() {
		super(true);
	}

	@Override
	public void play() {
		boolean switchedPlayer = false;
		if (Playback.getManager().replayPlayer != null && Playback.getManager().currentAppliedPlayer != Playback.getManager().replayPlayer) {
			Playback.getManager().replayPlayer.apply(false);
			switchedPlayer = true;
		}

		MinecraftClient.getInstance().openPauseMenu(false);

		if (switchedPlayer) {
			Playback.getManager().updateView(Playback.getManager().getView(), false);
		}
	}
}