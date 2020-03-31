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
	public Keyboard keyboard;
	private boolean cameraOnly;

	//Those states are just there to store the old values in MinecraftClient.
	private Screen currentScreen;
	private int attackCooldown;

	private HitResult crosshairTarget;
	private Entity targetedEntity;
	private int itemUseCooldown;
	private boolean windowFocus;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, PlayGameOptions options, Mouse mouse, Keyboard keyboard) {
		this.player = player;
		this.interactionManager = interactionManager;
		this.options = options;
		this.mouse = mouse;
		this.keyboard = keyboard;
		this.windowFocus = MinecraftClient.getInstance().isWindowFocused();
	}

	public PlayerFrame getAppliedPlayerFrame() {
		if(this.isActive()) {
			return this;
		} else {
			if(this == Playback.getManager().cameraPlayer) {
				return Playback.getManager().replayPlayer;
			} else {
				return Playback.getManager().cameraPlayer;
			}
		}

	}

	public void apply() {
		PlayerFrame prevFrame = Playback.getManager().currentAppliedPlayer;
		//commented out for now because the code after has to run at least once
		if(this == prevFrame) {
			return;
		}

		Playback.getManager().currentAppliedPlayer = this;

		if(prevFrame != null) {
			prevFrame.copyState();
		}

		if(!this.cameraOnly) {
			client.player = this.player;
			client.interactionManager = this.interactionManager;
			((IClientCaller)client).setOptions(this.options.getOptions());
			this.options.apply();
			boolean withCallback = this == Playback.getManager().cameraPlayer || (Playback.getManager().getView() == ReplayView.FIRST_PERSON && Playback.getManager().isCurrentlyAcceptingInputs());
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
		this.windowFocus = client.isWindowFocused();
	}

	public void applyState() {
		client.currentScreen = this.currentScreen;
		((IClientCaller)client).setAttackCooldown(this.attackCooldown);
		((IClientCaller) client).setItemUseCooldown(this.itemUseCooldown);
		client.crosshairTarget = this.crosshairTarget;
		client.targetedEntity = this.targetedEntity;
		((IClientCaller) client).setWindowFocusNoInjects(this.windowFocus);
	}

	public static PlayerFrame createFromExisting() {
		((PlayGameOptions.IKeyBindingCaller)client.options.keysAll[0]).resetStaticCollections();
		PlayGameOptions options = new PlayGameOptions();
		((IKeyboardInputCaller)client.player.input).setOptions(options.getOptions());
		Mouse mouse = new Mouse(client);
		return new PlayerFrame(client.player, client.interactionManager, options, mouse, new Keyboard(client));
	}

	public static PlayerFrame createNew() {
		ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client, client.getNetworkHandler());
		PlayGameOptions options = new PlayGameOptions(MinecraftClient.getInstance().options);
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler(), interactionManager, options);
		Mouse mouse = new Mouse(client);
		return new PlayerFrame(player, interactionManager, options, mouse, new Keyboard(client));
	}

	public PlayerFrame cameraOnly() {
		this.cameraOnly = true;
		return this;
	}

	public void setWindowFocus(boolean windowFocus) {
		if (this == Playback.getManager().currentAppliedPlayer) {
			((IClientCaller)MinecraftClient.getInstance()).setWindowFocusNoInjects(windowFocus);
		}
		this.windowFocus = windowFocus;
	}

	public void onReplayFinished() {
		if (this == Playback.getManager().replayPlayer) {
			//copy the window focus over
			this.setWindowFocus(Playback.getManager().cameraPlayer.windowFocus);
			if (this == Playback.getManager().currentAppliedPlayer && Playback.getManager().getView() == ReplayView.FIRST_PERSON) {
				boolean withCallback = Playback.getManager().isCurrentlyAcceptingInputs();
				//set whether mouse is grabbed etc.
				((IClientCaller)client).setMouse(this.mouse, withCallback);
				((IClientCaller)client).setKeyboard(this.keyboard, withCallback);
			}
		}
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
		void setWindowFocusNoInjects(boolean windowFocus);
	}

	public interface IKeyboardInputCaller {
		void setOptions(GameOptions options);
	}

}
