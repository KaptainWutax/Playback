package kaptainwutax.playback.mixin.server;

import kaptainwutax.playback.Playback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract ServerNetworkIo getNetworkIo();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if(!Playback.isReplaying) return;
		this.getNetworkIo().tick();
		ci.cancel();
	}

}
