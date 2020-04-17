package kaptainwutax.playback.mixin.server.world;

import kaptainwutax.playback.Playback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "savePlayerData", at = @At("HEAD"), cancellable = true)
    private void noSave(ServerPlayerEntity player, CallbackInfo ci) {
        if (Playback.getManager().isOrWasReplaying()) ci.cancel();
    }
}
