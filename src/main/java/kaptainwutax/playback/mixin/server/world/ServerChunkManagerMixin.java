package kaptainwutax.playback.mixin.server.world;

import kaptainwutax.playback.Playback;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void noSave(boolean flush, CallbackInfo ci) {
        if (Playback.getManager().isOrWasReplaying()) ci.cancel();
    }
}
