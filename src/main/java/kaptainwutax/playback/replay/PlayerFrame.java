package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public class PlayerFrame {

	private static MinecraftClient client = MinecraftClient.getInstance();

	private ClientPlayerEntity player;
	private ClientPlayerInteractionManager interactionManager;
	public PlayGameOptions options;
	public Mouse mouse;
	public Keyboard keyboard;

	private boolean cameraOnly;
	public boolean wasTeleported;

	//Those states are just there to store the old values in MinecraftClient.
	private Screen currentScreen;
	private int attackCooldown;
	private HitResult crosshairTarget;
	private Entity targetedEntity;
	private int itemUseCooldown;
	private boolean windowFocus;

	private PlayerFrame(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, PlayGameOptions options, Mouse mouse, Keyboard keyboard, boolean windowFocus) {
		this.player = player;
		this.interactionManager = interactionManager;
		this.options = options;
		this.mouse = mouse;
		this.keyboard = keyboard;
		this.windowFocus = windowFocus;

		this.wasTeleported = false;
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

	public void apply(boolean allowSetCallback) {
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
			this.options.apply();
			boolean withCallback = allowSetCallback && !Playback.getManager().isOnlyAcceptingReplayedInputs();
			withCallback = withCallback && this == Playback.getManager().getPlayerFrameForView(Playback.getManager().view);
			((IClientCaller)client).setMouse(this.mouse, withCallback);
			((IClientCaller)client).setKeyboard(this.keyboard, withCallback);
			this.applyState();
		}

		client.setCameraEntity(this.player); //todo get rid of side effect of removing shaders (e.g. green creeper view)
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
		return new PlayerFrame(client.player, client.interactionManager, options, mouse, new Keyboard(client), Playback.getManager().recording.getStartState().getWindowFocus());
	}

	public static PlayerFrame createNew() {
		ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client, client.getNetworkHandler());
		PlayGameOptions options = new PlayGameOptions(MinecraftClient.getInstance().options);
		FakePlayer player = new FakePlayer(client, client.world, client.getNetworkHandler(), interactionManager, options, new KeyboardInput(options.getOptions()));
		Mouse mouse = new Mouse(client);
		return new PlayerFrame(player, interactionManager, options, mouse, new Keyboard(client), MinecraftClient.getInstance().isWindowFocused());
	}


	public void updatePlayerFrameOnRespawnOrDimensionChange(ClientPlayerEntity newReplayPlayerEntity) {
		if (Playback.getManager().currentAppliedPlayer != Playback.getManager().replayPlayer) {
			System.err.println("Skipping updating player frame on respawn because replay player isn't applied!");
			return;
		}

		if (this == Playback.getManager().cameraPlayer) {
			this.player = new FakePlayer(client, client.world, this.player.networkHandler, this.interactionManager, this.options, new KeyboardInput(options.getOptions()));
			//teleport to the replayPlayer again
			this.wasTeleported = false;
		} else if (this == Playback.getManager().replayPlayer) {
			this.player = newReplayPlayerEntity;
		} else {
			System.err.println("Updating unused player frame skipped");
		}
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

	public Screen getCurrentScreen() {
		if (this == Playback.getManager().currentAppliedPlayer) {
			return MinecraftClient.getInstance().currentScreen;
		}
		return currentScreen;
	}

	public void onReplayFinished() {
		if (this == Playback.getManager().replayPlayer) {
			//copy the window focus over
			this.setWindowFocus(Playback.getManager().cameraPlayer.windowFocus);
			boolean withCallback = !Playback.getManager().isOnlyAcceptingReplayedInputs();
			withCallback = withCallback && this == Playback.getManager().getPlayerFrameForView(Playback.getManager().view);
			//set whether mouse is grabbed etc.
			((IClientCaller)client).setMouse(this.mouse, withCallback);
			((IClientCaller)client).setKeyboard(this.keyboard, withCallback);
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
