package kaptainwutax.playback.replay;

import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();

	private ClientPlayerEntity player;
	private ClientPlayerInteractionManager interactionManager;
	private PlayGameOptions options;

	private boolean cameraOnly;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, PlayGameOptions options) {
		this.player = player;
		this.interactionManager = interactionManager;
		this.options = options;
	}

	public void apply() {
		if(!this.cameraOnly) {
			client.player = this.player;
			client.interactionManager = this.interactionManager;
			((IClientCaller)client).setOptions(this.options);
			this.options.apply();
		}

		client.setCameraEntity(this.player);
	}

	public static PlayerFrame createFromExisting() {
		((PlayGameOptions.IKeyBindingCaller)client.options.keysAll[0]).resetStaticCollections();
		PlayGameOptions options = new PlayGameOptions();
		((IKeyboardInputCaller)client.player.input).setOptions(options);
		return new PlayerFrame(client.player, client.interactionManager, options);
	}

	public static PlayerFrame createNew() {
		ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client, client.getNetworkHandler());
		((PlayGameOptions.IKeyBindingCaller)client.options.keysAll[0]).resetStaticCollections();
		PlayGameOptions options = new PlayGameOptions();
		//options.load(); //We'll have to do this at some point.
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler(), interactionManager, options);
		return new PlayerFrame(player, interactionManager, options);
	}

	public PlayerFrame cameraOnly() {
		this.cameraOnly = true;
		return this;
	}

	public boolean isActive() {
		if(this.cameraOnly)return MinecraftClient.getInstance().cameraEntity == this.player;
		return MinecraftClient.getInstance().player == this.player;
	}

	public ClientPlayerEntity getPlayer() {
		return player;
	}

	public interface IClientCaller {
		void setOptions(GameOptions options);
	}

	public interface IKeyboardInputCaller {
		void setOptions(GameOptions options);
	}

}
