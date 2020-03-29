package kaptainwutax.playback.replay.action;

import net.minecraft.client.MinecraftClient;

public class MouseAction extends Action {

	private int action;
	private double d1;
	private double d2;
	private int i1;
	private boolean windowFocused;
	private boolean cursorLocked;

	public MouseAction(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
		this.windowFocused = MinecraftClient.getInstance().isWindowFocused();
		this.cursorLocked = isCursorLocked;
	}

	@Override
	public void play() {
		((IMouse) client.mouse).execute(this.action, this.d1, this.d2, this.i1, this.windowFocused, this.cursorLocked);
	}

}