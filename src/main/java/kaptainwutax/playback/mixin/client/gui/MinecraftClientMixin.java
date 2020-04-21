package kaptainwutax.playback.mixin.client.gui;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "onResolutionChanged", at = @At(value = "RETURN"))
    private void resizeHud(CallbackInfo ci) {
        Playback.getManager().renderManager.replayHud.resize();
    }
}
