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
	private int attackCooldown;




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
		this.attackCooldown = ((IClientCaller)client).getAttackCooldown();
	}

	public void applyState() {
		client.currentScreen = this.currentScreen;
		((IClientCaller)client).setAttackCooldown(this.attackCooldown);
	}

	public PlayerFrame getAppliedPlayerFrame() {
		if (this.isActive()) {
			return this;
		} else {
			if (this == Playback.manager.cameraPlayer) {
				return Playback.manager.replayPlayer;
			} else {
				return Playback.manager.cameraPlayer;
			}
		}

	}

	public void apply() {
		PlayerFrame prevFrame = this.getAppliedPlayerFrame();
		if (this == prevFrame) {
			return;
		}
		if (prevFrame != null) {
			prevFrame.copyState();
		}

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

	public interface IClientCaller {
		int getAttackCooldown();
		void setAttackCooldown(int i);
	}

}
