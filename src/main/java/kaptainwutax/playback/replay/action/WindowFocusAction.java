package kaptainwutax.playback.replay.action;

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
		MinecraftClient.getInstance().onWindowFocusChanged(windowFocused);
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