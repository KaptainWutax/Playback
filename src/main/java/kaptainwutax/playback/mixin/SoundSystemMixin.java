package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void cancelIfSoundsNotWanted(SoundInstance soundInstance, CallbackInfo ci) {
        if (Playback.isCatchingUp) {
            ci.cancel();
        }
    }
}
