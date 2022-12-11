package kaptainwutax.playback.mixin.server.world;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin {

    @Inject(method = "savePlayerData", at = @At("HEAD"), cancellable = true)
    private void noSave(PlayerEntity playerEntity, CallbackInfo ci) {
        if (Playback.getManager().isOrWasReplaying()) ci.cancel();
    }

    @Inject(method = "loadPlayerData", at = @At("HEAD"), cancellable = true)
    private void noLoad(PlayerEntity playerEntity, CallbackInfoReturnable<NbtCompound> cir) {
        if (Playback.getManager().isInReplay()) cir.setReturnValue(null);
    }

}