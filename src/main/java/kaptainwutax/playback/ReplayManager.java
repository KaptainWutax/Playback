package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;

public class ReplayManager {

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	private ReplayView view = ReplayView.THIRD_PERSON;

	public void updateView(ReplayView view) {
		this.view = view;

		if(view == ReplayView.FIRST_PERSON) {
			if(this.replayPlayer == null) this.replayPlayer = PlayerFrame.createFromExisting();
			this.replayPlayer.apply();
		}

		if(view == ReplayView.THIRD_PERSON) {
			if(this.replayPlayer == null) this.replayPlayer = PlayerFrame.createFromExisting();
			if(this.cameraPlayer == null) this.cameraPlayer = PlayerFrame.createNew();
			this.cameraPlayer.apply();
		}
	}

	public ReplayView getView() {
		return this.view;
	}

}
