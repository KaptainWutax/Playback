package kaptainwutax.playback.capture.action;

import net.minecraft.client.MinecraftClient;

public class KeyAction implements IAction {

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
		((IKeyboard) MinecraftClient.getInstance().keyboard).execute(this.action, this.window, this.key, this.scancode, this.i, this.j);
	}

}
