package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();
	private ClientPlayerEntity player;
	private ClientPlayerInteractionManager interactionManager;
	private Screen currentScreen;



	private boolean cameraOnly;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager) {
		this.player = player;
		this.interactionManager = interactionManager;

		this.copyState();
	}

	public ClientPlayerEntity getPlayer() {
		return this.player;
	}

	public void copyState() {
		this.currentScreen = client.currentScreen;
	}

	public void applyState() {
		client.currentScreen = this.currentScreen;
	}

	public static PlayerFrame getAppliedPlayerFrame() {
		if (Playback.manager.cameraPlayer != null && MinecraftClient.getInstance().player == Playback.manager.cameraPlayer.getPlayer()) {
			return Playback.manager.cameraPlayer;
		} else {
			return Playback.manager.replayPlayer;
		}
	}

	public void apply() {
		getAppliedPlayerFrame().copyState();

		if(!this.cameraOnly) {
			client.player = this.player;
			client.interactionManager = this.interactionManager;
			//load state that was copied earlier
			this.applyState();
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
