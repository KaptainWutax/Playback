package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;

public class ReplayManager {

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	public PlayerFrame currentAppliedPlayer;

	public ReplayView view = ReplayView.THIRD_PERSON;

	public void updateView(ReplayView view) {
		this.view = view;
		if(this.replayPlayer == null)this.replayPlayer = PlayerFrame.createFromExisting();
		if(this.cameraPlayer == null)this.cameraPlayer = PlayerFrame.createNew();

		if(view == ReplayView.FIRST_PERSON) {
			this.replayPlayer.apply();
		} else if(view == ReplayView.THIRD_PERSON) {
			this.cameraPlayer.apply();
		}
	}

	public boolean isCurrentlyAcceptingInputs() {
		if (this.currentAppliedPlayer == null) {
			if (Playback.isReplaying) {
				System.out.println("Inputs with no player frame! Allowing them...");
			}
			return true;
		}

		return this.currentAppliedPlayer == cameraPlayer || Playback.isProcessingReplay || Playback.replayingHasFinished;
	}

	public ReplayView getView() {
		return this.view;
	}

	public PlayerFrame getPlayerFrameForView(ReplayView view) {
		if (view == ReplayView.FIRST_PERSON)
			return replayPlayer;
		else
			return cameraPlayer;
	}

	public void toggleView() {
		this.updateView(ReplayView.values()[(this.view.ordinal() + 1) % ReplayView.values().length]);

		//TODO: Maybe swapping this is better.
		MinecraftClient.getInstance().getToastManager().clear();

		//Teleport the camera player to the replay player.
		Playback.manager.cameraPlayer.getPlayer().updatePositionAndAngles(
				Playback.manager.replayPlayer.getPlayer().getX(),
				Playback.manager.replayPlayer.getPlayer().getY(),
				Playback.manager.replayPlayer.getPlayer().getZ(),
				Playback.manager.replayPlayer.getPlayer().yaw,
				Playback.manager.replayPlayer.getPlayer().pitch
		);
	}

}
