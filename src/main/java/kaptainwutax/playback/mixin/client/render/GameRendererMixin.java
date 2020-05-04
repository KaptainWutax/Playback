package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void updateCameraPath(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Playback.getManager().renderManager.updateCameraForCameraPath(Playback.getManager().tickCounter, tickDelta);
    }

    //Fix pauseOnLostFocus messing up the replay player by not being played or having different timing
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openPauseMenu(Z)V"))
    private void cancelPauseOnLostFocus(MinecraftClient minecraftClient, boolean bl) {
        if (Playback.getManager().isRecording()) {
            Playback.getManager().recording.getCurrentTickInfo().recordLostFocusPause();
        }

        if (Playback.getManager().isInReplay() && Playback.getManager().currentAppliedPlayer == Playback.getManager().replayPlayer) {
            return;
        }
        minecraftClient.openPauseMenu(bl);
    }
}
