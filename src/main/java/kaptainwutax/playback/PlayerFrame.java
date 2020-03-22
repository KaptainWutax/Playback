package kaptainwutax.playback;

import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();
	private ClientPlayerEntity player;
	private ClientPlayerInteractionManager interactionManager;
	private boolean cameraOnly;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager) {
		this.player = player;
		this.interactionManager = interactionManager;
	}

	public ClientPlayerEntity getPlayer() {
		return this.player;
	}

	public void apply() {
		if(!this.cameraOnly) {
			client.player = this.player;
			client.interactionManager = this.interactionManager;
		}

		client.setCameraEntity(this.player);
	}

	public static PlayerFrame createFromExisting() {
		return new PlayerFrame(client.player, client.interactionManager);
	}

	public static PlayerFrame createNew() {
		ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client, client.getNetworkHandler());
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler(), interactionManager);
		return new PlayerFrame(player, interactionManager);
	}

	public PlayerFrame cameraOnly() {
		this.cameraOnly = true;
		return this;
	}

	public boolean isActive() {
		if(this.cameraOnly) return MinecraftClient.getInstance().cameraEntity == this.player;
		return MinecraftClient.getInstance().player == this.player;
	}

}
