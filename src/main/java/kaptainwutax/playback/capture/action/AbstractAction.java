package kaptainwutax.playback.capture.action;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractAction {

	protected static MinecraftClient client = MinecraftClient.getInstance();

	public abstract void play();

}
