package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.render.RenderManager;
import kaptainwutax.playback.replay.render.ReplayCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements RenderManager.MutableCamera {
    private @Mutable @Final @Shadow Camera camera;

    @Inject(method = "renderWorld", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void updateCameraPath(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Playback.getManager().renderManager.updateCameraForCameraPath();
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/util/math/Matrix4f;)V"))
    private void rotateRoll(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (camera instanceof ReplayCamera) {
            matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(((ReplayCamera) camera).getRoll()));
        }
    }

    @Inject(method = "method_22973", at = @At("HEAD"), cancellable = true)
    private void replayCameraMatrix(Camera camera, float f, boolean bl, CallbackInfoReturnable<Matrix4f> cir) {
        if (camera instanceof ReplayCamera) {
            cir.setReturnValue(((ReplayCamera) camera).getBasicProjectionMatrix(f, bl));
        }
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
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
