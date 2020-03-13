package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;

public class MouseAction implements IAction {

	private int action;
	private long window;
	private double d1;
	private double d2;
	private int i1;

	public MouseAction(int action, long window, double d1, double d2, int i1) {
		this.action = action;
		this.window = window;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
	}

	@Override
	public void play() {
		Playback.allowInputs = true;
		((IMouse)MinecraftClient.getInstance().mouse).execute(this.action, this.window, this.d1, this.d2, this.i1);
		Playback.allowInputs = false;
	}

}