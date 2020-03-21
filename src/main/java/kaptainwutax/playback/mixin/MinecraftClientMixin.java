package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.capture.action.PacketAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements PacketAction.IConnectionGetter {

	@Shadow public ClientWorld world;
	@Shadow public ClientPlayerEntity player;

	@Shadow private boolean windowFocused;
	@Shadow private boolean paused;

	@Shadow
	private ClientConnection connection;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickStart(CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.replayPlayer.apply();
		}

		if(this.world != null) {
			Playback.update(this.paused);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tickEnd(CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.updateView(Playback.manager.getView());
		}
	}


	/*
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = At.Shift.BEFORE))
	private void renderRunTasksBefore(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.updateView(Playback.manager.getView());
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = At.Shift.AFTER))
	private void renderRunTasksAfter(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.replayPlayer.apply();
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
	private void renderGameRenderBefore(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.updateView(Playback.manager.getView());
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
	private void renderGameRenderAfter(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.replayPlayer.apply();
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V", shift = At.Shift.BEFORE))
	private void renderUpdateMouseBefore(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.updateView(Playback.manager.getView());
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V", shift = At.Shift.AFTER))
	private void renderUpdateMouseAfter(boolean tick, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
			Playback.manager.replayPlayer.apply();
		}
	}*/

	//Intended to fix inconsistency due to differing fps during replay and recording
	//@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
	//private int MathMinFixed(int i, int i1) {
	//	return 1;
	//}


	//During first person replay pretend window has focus so recorded mouse actions always get processed
	@Inject(method = "onWindowFocusChanged(Z)V", at = @At("RETURN"))
	private void setWindowFocussedDuringReplay(boolean focused, CallbackInfo ci) {
		if (Playback.isReplaying && Playback.mode == ReplayView.FIRST_PERSON)
			this.windowFocused = true;
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

}
