package kaptainwutax.playback.capture.action;

public class KeyAction extends Action {

	private int action;
	private long window;
	private int key;
	private int scancode;
	private int i;
	private int j;

	public KeyAction(int action, long window, int key, int scancode, int i, int j) {
		this.action = action;
		this.window = window;
		this.key = key;
		this.scancode = scancode;
		this.i = i;
		this.j = j;
	}

	@Override
	public void play() {
		((IKeyboard) client.keyboard).execute(this.action, this.window, this.key, this.scancode, this.i, this.j);
	}

}
