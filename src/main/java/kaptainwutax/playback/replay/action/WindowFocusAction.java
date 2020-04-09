package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;

public class WindowFocusAction extends Action {

	private boolean windowFocused;

	public WindowFocusAction() {}

	public WindowFocusAction(boolean focused) {
		this.windowFocused = focused;
	}

	@Override
	public void play() {
		if (Playback.getManager().replayPlayer != null)
			Playback.getManager().replayPlayer.setWindowFocus(windowFocused);
		//else: replayPlayer initialization queries the initial window focus
	}

	public boolean getFocus() {
		return this.windowFocused;
	}

	@Override
	public Type getType() {
		return Type.WINDOW_FOCUS;
	}

	@Override
	public void read(PacketByteBuf buf) {
		windowFocused = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(windowFocused);
	}

}