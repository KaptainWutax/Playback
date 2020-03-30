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
		Playback.getManager().replayPlayer.setWindowFocus(windowFocused);
	}

	@Override
	public Type getType() {
		return Type.MOUSE;
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