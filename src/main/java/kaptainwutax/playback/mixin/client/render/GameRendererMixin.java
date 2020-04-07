package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void updateCameraPath(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Playback.getManager().renderManager.updateCameraForCameraPath(Playback.getManager().recording.currentTick, tickDelta);
    }
}
