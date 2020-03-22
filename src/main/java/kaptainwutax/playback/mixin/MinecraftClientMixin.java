package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickStart(CallbackInfo ci) {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.replayPlayer == null) {
				Playback.manager.updateView(Playback.mode);
			}

			if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
				Playback.manager.replayPlayer.apply();
			}

			Playback.update(this.paused);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tickEnd(CallbackInfo ci) {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.replayPlayer != null) {
				Playback.manager.updateView(Playback.manager.getView());
			}
		}

		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.cameraPlayer != null) {
			this.world.tickEntity(Playback.manager.cameraPlayer.getPlayer());
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

}
