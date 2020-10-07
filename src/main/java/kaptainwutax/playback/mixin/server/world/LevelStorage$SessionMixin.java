package kaptainwutax.playback.mixin.server.world;

import kaptainwutax.playback.Playback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelStorage.Session.class)
public class LevelStorage$SessionMixin {

    @Inject(method = "backupLevelDataFile(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/SaveProperties;)V", at = @At("HEAD"), cancellable = true)
    private void noSave(DynamicRegistryManager dynamicRegistryManager, SaveProperties saveProperties, CallbackInfo ci) {
        if (Playback.getManager().isOrWasReplaying()) ci.cancel();
    }

    @Inject(method = "backupLevelDataFile(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/SaveProperties;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), cancellable = true)
    private void noSave(DynamicRegistryManager dynamicRegistryManager, SaveProperties saveProperties, CompoundTag compoundTag, CallbackInfo ci) {
        if (Playback.getManager().isOrWasReplaying()) ci.cancel();
    }
}
