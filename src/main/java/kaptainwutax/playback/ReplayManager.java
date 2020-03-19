package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;

public class ReplayManager {

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	private PlayerFrame unusedPlayer;//player to imitate effect of cameraPlayer on entity hashmap
	private ReplayView view = ReplayView.THIRD_PERSON;

	public void updateView(ReplayView view) { //this only runs once per replay
		this.view = view;

		if(view == ReplayView.FIRST_PERSON) {
			if(this.replayPlayer == null)this.replayPlayer = PlayerFrame.createFromExisting();
			if(this.unusedPlayer == null)this.unusedPlayer = PlayerFrame.createNew();
			this.replayPlayer.apply();
		}

		if(view == ReplayView.THIRD_PERSON) {
			if(this.replayPlayer == null)this.replayPlayer = PlayerFrame.createFromExisting();
			if(this.cameraPlayer == null)this.cameraPlayer = PlayerFrame.createNew();
			this.cameraPlayer.apply();
		}
	}

	public ReplayView getView() {
		return this.view;
	}



}
