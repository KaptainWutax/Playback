package kaptainwutax.playback.mixin.client;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.PlayerFrame;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements PacketAction.IConnectionGetter, FakePlayer.IClientCaller, PlayerFrame.IClientCaller, PlayGameOptions.IClientCaller {

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

	private void applyCameraPlayerIfNecessary() {
		if(this.world != null && Playback.getManager().isReplaying()) {
			Playback.getManager().updateView(Playback.getManager().getView());
		}
	}

	private void applyReplayPlayerIfNecessary() {
		if(this.world != null && Playback.getManager().isReplaying()) {
			if(Playback.getManager().replayPlayer == null) {
				Playback.getManager().updateView(Playback.getManager().getView());
			}

			Playback.getManager().replayPlayer.apply();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickStart(CallbackInfo ci) {
		if(this.world != null) {
			applyReplayPlayerIfNecessary();
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
		if(this.world != null) {
			applyCameraPlayerIfNecessary();
			if (!Playback.getManager().isReplaying()) {
				return;
			}

			if(Playback.getManager().getView() == ReplayView.THIRD_PERSON) {
				this.world.tickEntity(Playback.getManager().cameraPlayer.getPlayer());
			}

			Playback.getManager().cameraPlayer.options.apply();

			boolean shouldToggleView = false;

			//TODO: Add spam protection.
			if(KeyBindings.TOGGLE_VIEW.isPressed()) {
				while(KeyBindings.TOGGLE_VIEW.wasPressed()) {
					shouldToggleView = true;
				}

			}

			if(Playback.getManager().view == ReplayView.FIRST_PERSON) {
				Playback.getManager().replayPlayer.options.apply();
			}

			if(shouldToggleView) {
				Playback.getManager().toggleView();
			}
		}
	}

	//Record window focus during recording and prevent calls affecting the replay player during the replay.
	@Inject(method = "onWindowFocusChanged", at = @At("HEAD"), cancellable = true)
	private void recordOrRedirectWindowFocus(boolean focused, CallbackInfo ci) {
		if (this.windowFocused == focused) {
			return;
		}
		if (Playback.getManager().isRecording()) {
			Playback.getManager().recording.getCurrentTickInfo().recordWindowFocus(focused);
			return;
		}

		if (Playback.getManager().cameraPlayer != null && Playback.getManager().isReplaying()) {
			ci.cancel();
			Playback.getManager().cameraPlayer.setWindowFocus(focused);
			if (Playback.getManager().replayingHasFinished) {
				Playback.getManager().replayPlayer.setWindowFocus(focused);
			}
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
		if(Playback.getManager().isReplaying() && Playback.getManager().isProcessingReplay) {
			ci.cancel();
		}
	}

	@Redirect(method = "doItemUse", require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V"))
	private void resetEquipProgressIfPlayerIsCamera(HeldItemRenderer heldItemRenderer, Hand hand) {
		if (!Playback.getManager().isReplaying() || (Playback.getManager().getView() == ReplayView.FIRST_PERSON)
				|| ((Playback.getManager().cameraPlayer != null) && (this.player == Playback.getManager().cameraPlayer.getPlayer()))) {
			heldItemRenderer.resetEquipProgress(hand);
		}
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
}
