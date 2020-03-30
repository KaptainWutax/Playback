package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();

	private ClientPlayerEntity player;
	private ClientPlayerInteractionManager interactionManager;
	private PlayGameOptions options;
	public Mouse mouse;
	private Keyboard keyboard;

	//Those states are just there to store the old values in MinecraftClient.
	private Screen currentScreen;
	private int attackCooldown;

	private boolean cameraOnly;
	private HitResult crosshairTarget;
	private Entity targetedEntity;
	private int itemUseCooldown;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, PlayGameOptions options, Mouse mouse, Keyboard keyboard) {
		this.player = player;
		this.interactionManager = interactionManager;
		this.options = options;
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	public PlayerFrame getAppliedPlayerFrame() {
		if(this.isActive()) {
			return this;
		} else {
			if(this == Playback.manager.cameraPlayer) {
				return Playback.manager.replayPlayer;
			} else {
				return Playback.manager.cameraPlayer;
			}
		}

	}

	public void apply() {
		PlayerFrame prevFrame = Playback.manager.currentAppliedPlayer;
		//commented out for now because the code after has to run at least once
		if(this == prevFrame) {
			return;
		}
		Playback.manager.currentAppliedPlayer = this;

		if(prevFrame != null) {
			prevFrame.copyState();
		}

		if(!this.cameraOnly) {
			client.player = this.player;
			client.interactionManager = this.interactionManager;
			((IClientCaller)client).setOptions(this.options);
			this.options.apply();
			boolean withCallback = this == Playback.manager.cameraPlayer || (Playback.manager.getView() == ReplayView.FIRST_PERSON && Playback.manager.isCurrentlyAcceptingInputs());
			((IClientCaller)client).setMouse(this.mouse, withCallback);
			((IClientCaller)client).setKeyboard(this.keyboard, withCallback);
			this.applyState();
		}

		client.setCameraEntity(this.player);
	}

	public void copyState() {
		this.currentScreen = client.currentScreen;
		this.attackCooldown = ((IClientCaller)client).getAttackCooldown();
		this.itemUseCooldown = ((IClientCaller) client).getItemUseCooldown();
		this.crosshairTarget = client.crosshairTarget;
		this.targetedEntity = client.targetedEntity;
	}

	public void applyState() {
		client.currentScreen = this.currentScreen;
		((IClientCaller)client).setAttackCooldown(this.attackCooldown);
		((IClientCaller) client).setItemUseCooldown(this.itemUseCooldown);
		client.crosshairTarget = this.crosshairTarget;
		client.targetedEntity = this.targetedEntity;
	}

	public static PlayerFrame createFromExisting() {
		((PlayGameOptions.IKeyBindingCaller)client.options.keysAll[0]).resetStaticCollections();
		PlayGameOptions options = new PlayGameOptions();
		((IKeyboardInputCaller)client.player.input).setOptions(options);
		Mouse mouse = new Mouse(client);
		return new PlayerFrame(client.player, client.interactionManager, options, mouse, new Keyboard(client));
	}

	public static PlayerFrame createNew() {
		ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client, client.getNetworkHandler());
		((PlayGameOptions.IKeyBindingCaller)client.options.keysAll[0]).resetStaticCollections();
		PlayGameOptions options = new PlayGameOptions();
		//options.load(); //We'll have to do this at some point.
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler(), interactionManager, options);
		Mouse mouse = new Mouse(client);
		return new PlayerFrame(player, interactionManager, options, mouse, new Keyboard(client));
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
		int getAttackCooldown();
		int getItemUseCooldown();

		void setOptions(GameOptions options);
		void setMouse(Mouse mouse, boolean withCallback);
		void setKeyboard(Keyboard keyboard, boolean withCallback);
		void setAttackCooldown(int attackCooldown);
		void setItemUseCooldown(int itemUseCooldown);
	}

	public interface IKeyboardInputCaller {
		void setOptions(GameOptions options);
	}

}
