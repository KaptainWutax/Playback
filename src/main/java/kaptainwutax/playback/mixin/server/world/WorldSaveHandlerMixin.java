package kaptainwutax.playback.mixin.server.world;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin {
    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;)V", at = @At("HEAD"), cancellable = true)
    private void noSave(LevelProperties levelProperties, CallbackInfo ci) {
        if (Playback.getManager().isReplaying()) ci.cancel();
    }

    @Inject(method = "saveWorld(Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), cancellable = true)
    private void noSave(LevelProperties levelProperties, CompoundTag tag, CallbackInfo ci) {
        if (Playback.getManager().isReplaying()) ci.cancel();
    }

    @Inject(method = "savePlayerData", at = @At("HEAD"), cancellable = true)
    private void noSave(PlayerEntity playerEntity, CallbackInfo ci) {
        if (Playback.getManager().isReplaying()) ci.cancel();
    }

    @Inject(method = "loadPlayerData", at = @At("HEAD"), cancellable = true)
    private void noLoad(PlayerEntity playerEntity, CallbackInfoReturnable<CompoundTag> cir) {
        if (Playback.getManager().isReplaying()) cir.setReturnValue(null);
    }
}
