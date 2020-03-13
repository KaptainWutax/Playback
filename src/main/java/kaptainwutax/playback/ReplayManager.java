package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;

public class ReplayManager {

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	private ReplayView view = ReplayView.THIRD_PERSON;

	public void updateView(ReplayView view) {
		this.view = view;

		this.replayPlayer.apply();

		if(view == ReplayView.THIRD_PERSON) {
			this.cameraPlayer.apply();
		}
	}

	public ReplayView getView() {
		return this.view;
	}

}
