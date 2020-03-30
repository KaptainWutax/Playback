package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;

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
			System.out.println("Inputs with no player frame! Allowing them...");
			return true;
		}

		return this.currentAppliedPlayer == cameraPlayer || Playback.isProcessingReplay || Playback.replayingHasFinished;
	}

	public ReplayView getView() {
		return this.view;
	}

}
