package kaptainwutax.playback.replay.action.first;

import kaptainwutax.playback.replay.action.Action;

public class MouseAction extends Action {

	private int action;
	private double d1;
	private double d2;
	private int i1;

	public MouseAction(int action, double d1, double d2, int i1) {
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
	}

	@Override
	public void play() {
		((IMouse) client.mouse).execute(this.action, this.d1, this.d2, this.i1);
	}

}