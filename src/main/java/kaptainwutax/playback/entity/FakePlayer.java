package kaptainwutax.playback.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.world.GameMode;

public class FakePlayer extends ClientPlayerEntity {

	public FakePlayer(MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		super(client, clientWorld, clientPlayNetworkHandler, null, null);
		GameMode.SPECTATOR.setAbilitites(this.abilities);
		this.input = new KeyboardInput(client.options);
	}

}
