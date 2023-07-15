package kaptainwutax.playback.mixin.client.fixes.crashes.narrator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(
            method = "isNarratorActive", at = @At("HEAD"), cancellable = true
    )
    private void isNarratorNonNull(CallbackInfoReturnable<Boolean> cir) {
        if (this.client == null || this.client.getNarratorManager() == null) {
            cir.setReturnValue(false);
        }
    }
}
