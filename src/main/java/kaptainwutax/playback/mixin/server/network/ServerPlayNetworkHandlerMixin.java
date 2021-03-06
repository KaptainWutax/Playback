package kaptainwutax.playback.mixin.server.network;

import kaptainwutax.playback.Playback;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

	/**
	 * Disables the anti-cheat in replay worlds.
	 **/
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void tick(CallbackInfo ci) {
		if(Playback.getManager().isInReplay()) {
			ci.cancel();
		}
	}

}
