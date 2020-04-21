package kaptainwutax.playback.mixin.client;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.gui.ReplayHudScreen;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.PlayerFrame;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.replay.action.SetPausedAction;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements PacketAction.IConnectionGetter, FakePlayer.IClientCaller, PlayerFrame.IClientCaller, PlayGameOptions.IClientCaller, SetPausedAction.ClientSetPause {

	private Keyboard callbackKeyboard;
	private Mouse callbackMouse;

	@Shadow
	public ClientWorld world;
	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	private boolean windowFocused;
	@Shadow
	private boolean paused;

	@Shadow
	private ClientConnection connection;

	@Shadow
	protected abstract void handleInputEvents();

	@Shadow protected int attackCooldown;

	@Mutable
	@Shadow @Final public GameOptions options;

	@Mutable
	@Shadow @Final public Mouse mouse;

	@Mutable
	@Shadow @Final public Keyboard keyboard;

	@Shadow private int itemUseCooldown;

	@Shadow @Final private Window window;

	@Shadow private float pausedTickDelta;

	@Shadow public abstract float getTickDelta();

	@Shadow @Nullable private IntegratedServer server;

	private void applyCameraPlayerIfNecessary() {
		if(this.world != null && Playback.getManager().isInReplay()) {
			Playback.getManager().updateView(Playback.getManager().getView(), true);
		}
	}

	private void applyReplayPlayerIfNecessary() {
		if(this.world != null && Playback.getManager().isInReplay()) {
			if(Playback.getManager().replayPlayer == null) {
				Playback.getManager().updateView(Playback.getManager().getView(), true);
			}

			Playback.getManager().replayPlayer.apply(true);
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(boolean tick, CallbackInfo ci) {
		if(Playback.getManager().isInReplay() && (Playback.getManager().isPaused() || !Playback.getManager().replayingHasFinished && Playback.getManager().recording.isTickPaused())) {
			this.paused = true;
			this.pausedTickDelta = 0;
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)V", shift = At.Shift.AFTER), cancellable = true)
	private void renderRunTasks(boolean tick, CallbackInfo ci) {
		applyReplayPlayerIfNecessary();
		Playback.getManager().tickFrame(this.paused, this.getTickDelta());
		applyCameraPlayerIfNecessary();
	}

	@Inject(method = "isIntegratedServerRunning", at = @At(value = "HEAD"), cancellable = true)
	private void replayIntegratedServerRunning(CallbackInfoReturnable<Boolean> cir) {
		if (Playback.getManager().isInReplay()) {
			cir.setReturnValue(!Playback.getManager().recording.isSinglePlayerRecording() && this.server != null);
		}
	}

	@Inject(method = "isInSingleplayer", at = @At(value = "HEAD"), cancellable = true)
	private void replayIsInSingleplayer(CallbackInfoReturnable<Boolean> cir) {
		if (Playback.getManager().isInReplay()) {
			cir.setReturnValue(Playback.getManager().recording.isSinglePlayerRecording());
		}
	}
	//todo check special cases for open to lan

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tickStart(CallbackInfo ci) {
		if(this.world != null) {
			applyReplayPlayerIfNecessary();

			if(Playback.getManager().isInReplay() && Playback.getManager().isPaused()) {
				this.runEndTickLogic();
				ci.cancel();
				return;
			}

			Playback.getManager().tick(this.paused);
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;tick()V", shift = At.Shift.BEFORE))
	private void tickHudStart(CallbackInfo ci) {
		applyCameraPlayerIfNecessary();
	}
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;tick()V", shift = At.Shift.AFTER))
	private void tickHudEnd(CallbackInfo ci) {
		applyReplayPlayerIfNecessary();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tick()V", shift = At.Shift.BEFORE))
	private void tickRendererStart(CallbackInfo ci) {
		applyCameraPlayerIfNecessary();
	}
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tick()V", shift = At.Shift.AFTER))
	private void tickRendererEnd(CallbackInfo ci) {
		applyReplayPlayerIfNecessary();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tickEnd(CallbackInfo ci) {
		this.runEndTickLogic();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V"))
	private void cancelExtraUpdateMouse(Mouse mouse) {
		if (Playback.getManager().isInReplay() && (Playback.getManager().replayPlayer != null && Playback.getManager().replayPlayer.isActive()))
			return; //don't execute because updateMouse is already being replayed
		mouse.updateMouse();
	}

	protected void runEndTickLogic() {
		if(this.world != null) {
			applyCameraPlayerIfNecessary();
			if (!Playback.getManager().isInReplay()) {
				return;
			}

			if(Playback.getManager().getView() == ReplayView.THIRD_PERSON) {
				this.world.tickEntity(Playback.getManager().cameraPlayer.getPlayer());
			}
			//todo why is swapping here necessary
			Playback.getManager().cameraPlayer.options.apply();

			boolean shouldToggleView = false;
			boolean shouldTogglePause = false;
			boolean shouldOpenHudScreen =  false;

			if(KeyBindings.TOGGLE_VIEW.isPressed()) {
				while(KeyBindings.TOGGLE_VIEW.wasPressed()) {
					shouldToggleView = true;
				}
			}

			if(KeyBindings.TOGGLE_PAUSE.isPressed()) {
				while(KeyBindings.TOGGLE_PAUSE.wasPressed()) {
					shouldTogglePause = true;
				}
			}

			if(KeyBindings.OPEN_REPLAY_HUD.isPressed()) {
				while(KeyBindings.OPEN_REPLAY_HUD.wasPressed()) {
					shouldOpenHudScreen = true;
				}
			}

			if(Playback.getManager().view == ReplayView.FIRST_PERSON) {
				Playback.getManager().replayPlayer.options.apply();
			}

			if(shouldToggleView) {
				Playback.getManager().toggleView();
			}

			if(shouldTogglePause) {
				Playback.getManager().togglePause();
			}

			//open for the camera player, not replay player
			if (shouldOpenHudScreen && !Playback.getManager().isOnlyAcceptingReplayedInputs()) {
				if (MinecraftClient.getInstance().currentScreen instanceof ReplayHudScreen)
					MinecraftClient.getInstance().openScreen(null);
				else
					MinecraftClient.getInstance().openScreen(Playback.getManager().renderManager.replayHud.getScreen());
			}
		}
	}

	//Record window focus during recording and prevent calls affecting the replay player during the replay.
	@Inject(method = "onWindowFocusChanged", at = @At("HEAD"), cancellable = true)
	private void recordOrRedirectWindowFocus(boolean focused, CallbackInfo ci) {
		if (this.windowFocused == focused) {
			return;
		}
		if (Playback.getManager().isRecording() && this.world != null) {
			Playback.getManager().recording.getCurrentTickInfo().recordWindowFocus(focused);
			return;
		}

		if (Playback.getManager().isInReplay()) {
			ci.cancel();
			if (Playback.getManager().cameraPlayer != null) {
				Playback.getManager().cameraPlayer.setWindowFocus(focused);
			}
			if (Playback.getManager().replayingHasFinished) {
				Playback.getManager().replayPlayer.setWindowFocus(focused);
			}
		}
	}
	//Record this.paused changing
	@Inject(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;isPauseScreen()Z")),
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;paused:Z", ordinal = 1, shift = At.Shift.BEFORE),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void recordPauseChanged(boolean tick, CallbackInfo ci, boolean bl) {
		if (Playback.getManager().isRecording()) {
			Playback.getManager().recording.getCurrentTickInfo().recordPauseChanged(bl);
		}
	}



	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

	@Override
	public void fakeHandleInputEvents() {
		this.handleInputEvents();
	}

	@Inject(method = "openPauseMenu", at = @At("HEAD"), cancellable = true)
	public void openPauseMenu(CallbackInfo ci) {
		if(Playback.getManager().isInReplay() && Playback.getManager().isProcessingReplay) {
			//ci.cancel();
		}
	}

	@Redirect(method = "doItemUse", require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V"))
	private void resetEquipProgressIfPlayerIsCamera(HeldItemRenderer heldItemRenderer, Hand hand) {
		if (!Playback.getManager().isInReplay() || (Playback.getManager().getView() == ReplayView.FIRST_PERSON)
				|| ((Playback.getManager().cameraPlayer != null) && (this.player == Playback.getManager().cameraPlayer.getPlayer()))) {
			heldItemRenderer.resetEquipProgress(hand);
		}
	}

	@Redirect(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	private void noLoadingScreen(MinecraftClient client, Screen screen) {
		if (!Playback.getManager().isInReplay()) client.openScreen(screen);
	}

	@Redirect(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	private void noLoadingScreen2(MinecraftClient client, Screen screen) {
		if (!Playback.getManager().isInReplay()) client.openScreen(screen);
	}

	@Override
	public int getAttackCooldown() {
		return this.attackCooldown;
	}

	@Override
	public int getItemUseCooldown() {
		return this.itemUseCooldown;
	}

	@Override
	public void setOptions(GameOptions options) {
		this.options = options;
	}

	@Override
	public void setMouse(Mouse mouse, boolean withCallbacks) {
		if (withCallbacks && mouse != this.callbackMouse) {
			this.callbackMouse = mouse;
			mouse.setup(this.window.getHandle());
			InputUtil.setCursorParameters(MinecraftClient.getInstance().getWindow().getHandle(), mouse.isCursorLocked() ? 212995 : 212993, mouse.getX(), mouse.getY());
		}
		this.mouse = mouse;
	}

	@Override
	public void setKeyboard(Keyboard keyboard, boolean withCallbacks) {
		if (withCallbacks && keyboard != this.callbackKeyboard) {
			this.callbackKeyboard = keyboard;
			keyboard.setup(this.window.getHandle());
		}
		this.keyboard = keyboard;
	}

	@Override
	public void setAttackCooldown(int attackCooldown) {
		this.attackCooldown = attackCooldown;
	}
	@Override
	public void setItemUseCooldown(int itemUseCooldown) {
		this.itemUseCooldown = itemUseCooldown;
	}

	@Override
	public void setWindowFocusNoInjects(boolean windowFocused) {
		this.windowFocused = windowFocused;
	}

	@Override
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
}
