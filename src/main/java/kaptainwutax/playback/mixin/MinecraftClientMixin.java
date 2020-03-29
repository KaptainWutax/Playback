package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements PacketAction.IConnectionGetter, FakePlayer.IClientCaller {

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

	private void applyCameraPlayerIfNecessary() {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
				Playback.manager.updateView(Playback.manager.getView());
			}
		}
	}

	private void applyReplayPlayerIfNecessary() {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.replayPlayer == null) {
				Playback.manager.updateView(Playback.mode);
			}

			if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
				Playback.manager.replayPlayer.apply();
			}
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickStart(CallbackInfo ci) {
		if(this.world != null) {
			if(Playback.isCatchingUp) {
				this.paused = false;
			}

			applyReplayPlayerIfNecessary();
			Playback.update(this.paused);
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V", shift = At.Shift.BEFORE))
	private void tickTargetedEntityStart(CallbackInfo ci) {
		//applyCameraPlayerIfNecessary();
	}
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V", shift = At.Shift.AFTER))
	private void tickTargetedEntityEnd(CallbackInfo ci) {
		//applyReplayPlayerIfNecessary();
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

			if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.cameraPlayer != null) {
				this.world.tickEntity(Playback.manager.cameraPlayer.getPlayer());
			}

			if(Playback.isReplaying && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), KeyBindings.TOGGLE_VIEW.getBoundKey().getKeyCode())) {
				while(KeyBindings.TOGGLE_VIEW.wasPressed()) {
				}
				if(!Playback.isCatchingUp) Playback.toggleView();
			}
		}
	}

	//During first person replay pretend window has focus so recorded mouse actions always get processed
	@Inject(method = "onWindowFocusChanged", at = @At("RETURN"))
	private void setWindowFocusedDuringReplay(boolean focused, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.mode == ReplayView.FIRST_PERSON) {
			this.windowFocused = true;
		}
	}

	@Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
	private void openScreen(Screen screen, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.isProcessingReplay) {
			if(screen != null) {
				Playback.recording.getCurrentTickInfo().third.getKeyAction().playUnpressAll();
			}

			ci.cancel();
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
		if(Playback.isReplaying && Playback.isProcessingReplay) {
			ci.cancel();
		}
	}

	@Redirect(method = "doItemUse", require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V"))
	private void resetEquipProgressIfPlayerIsCamera(HeldItemRenderer heldItemRenderer, Hand hand) {
		if (!Playback.isReplaying || (Playback.mode == ReplayView.FIRST_PERSON) || ((Playback.manager.cameraPlayer != null) && (this.player == Playback.manager.cameraPlayer.getPlayer()))) {
			heldItemRenderer.resetEquipProgress(hand);
		}
	}

}
