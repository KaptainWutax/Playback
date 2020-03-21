package kaptainwutax.playback;

import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();
	private ClientPlayerEntity player;
	private boolean cameraOnly;

	private PlayerFrame(ClientPlayerEntity player) {
		this.player = player;
	}

	public ClientPlayerEntity getPlayer() {
		return this.player;
	}

	public void apply() {
		if(!this.cameraOnly)client.player = this.player;
		client.setCameraEntity(this.player);
	}

	public static PlayerFrame createFromExisting() {
		return new PlayerFrame(client.player);
	}

	public static PlayerFrame createNew() {
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler());
		client.world.addPlayer(-1, player);
		return new PlayerFrame(player);
	}

	public PlayerFrame cameraOnly() {
		this.cameraOnly = true;
		return this;
	}

	public boolean isActive() {
		if(this.cameraOnly)return MinecraftClient.getInstance().cameraEntity == this.player;
		return MinecraftClient.getInstance().player == this.player;
	}

}
