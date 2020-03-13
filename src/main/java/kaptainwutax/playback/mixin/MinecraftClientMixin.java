package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin  {

	@Shadow public ClientWorld world;
	@Shadow public ClientPlayerEntity player;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null) {
				this.player = Playback.manager.replayPlayer.getPlayer();
			}

			Playback.update();
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void tickReturn(CallbackInfo ci) {
		if(this.world != null) {
			if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null) {
				this.player = Playback.manager.cameraPlayer.getPlayer();
			}
		}
	}

}
