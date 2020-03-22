package kaptainwutax.playback.replay.action.first;

import kaptainwutax.playback.replay.action.Action;

public class KeyAction extends Action {

	private int action;
	private int key;
	private int scanCode;
	private int i;
	private int j;

	public KeyAction(int action, int key, int scanCode, int i, int j) {
		this.action = action;
		this.key = key;
		this.scanCode = scanCode;
		this.i = i;
		this.j = j;
	}

	@Override
	public void play() {
		((IKeyboard) client.keyboard).execute(this.action, this.key, this.scanCode, this.i, this.j);
	}

}
