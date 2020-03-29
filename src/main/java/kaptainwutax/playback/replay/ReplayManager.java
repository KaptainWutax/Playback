package kaptainwutax.playback.replay;

public class ReplayManager {

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
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

	public ReplayView getView() {
		return this.view;
	}

}
