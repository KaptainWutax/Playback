package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.network.PacketByteBuf;

public class WindowFocusAction extends Action {

	private boolean windowFocused;

	public WindowFocusAction() {
		super(false);
	}

	public WindowFocusAction(boolean focused) {
		this();
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
	public void read(PacketByteBuf buf) {
		windowFocused = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(windowFocused);
	}

}