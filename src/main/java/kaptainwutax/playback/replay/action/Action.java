package kaptainwutax.playback.replay.action;

import net.minecraft.client.MinecraftClient;

public abstract class Action {

	protected static MinecraftClient client = MinecraftClient.getInstance();

	public abstract void play();

}