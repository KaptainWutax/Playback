package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.render.RenderManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Shadow public abstract int getFramebufferWidth();
    @Shadow public abstract int getFramebufferHeight();

    // Makes sure everything external gets the correct framebuffer size during rendering

    @Inject(method = "getFramebufferWidth", at = @At("HEAD"), cancellable = true)
    private void replaceFbWidth(CallbackInfoReturnable<Integer> cir) {
        RenderManager render = Playback.getManager().renderManager;
        if (render.isRendering()) cir.setReturnValue(render.getFramebuffer().viewportWidth);
    }

    @Inject(method = "getFramebufferHeight", at = @At("HEAD"), cancellable = true)
    private void replaceFbHeight(CallbackInfoReturnable<Integer> cir) {
        RenderManager render = Playback.getManager().renderManager;
        if (render.isRendering()) cir.setReturnValue(render.getFramebuffer().viewportHeight);
    }

    // Makes sure that the scale factor is calculated according to the corrected framebuffer size

    @Redirect(method = "calculateScaleFactor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/Window;framebufferWidth:I"))
    private int replaceFbWidthForScaleFactorCalc(Window window) {
        return window.getFramebufferWidth();
    }

    @Redirect(method = "calculateScaleFactor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/Window;framebufferHeight:I"))
    private int replaceFbHeightForScaleFactorCalc(Window window) {
        return window.getFramebufferHeight();
    }

    @Redirect(method = "setScaleFactor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/Window;framebufferWidth:I"))
    private int replaceFbWidthForSetScaleFactor(Window window) {
        return window.getFramebufferWidth();
    }

    @Redirect(method = "setScaleFactor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/Window;framebufferHeight:I"))
    private int replaceFbHeightForSetScaleFactor(Window window) {
        return window.getFramebufferHeight();
    }
}
