package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

	/**
	 * Trick the server to think spawn is always loaded.
	 * */
	@Inject(method = "getTotalChunksLoadedCount", at = @At("HEAD"), cancellable = true)
	private void getTotalChunksLoadedCount(CallbackInfoReturnable<Integer> ci) {
		if(Playback.isReplaying)ci.setReturnValue(441);
	}

}
