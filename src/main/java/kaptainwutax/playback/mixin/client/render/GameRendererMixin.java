package kaptainwutax.playback.mixin.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import kaptainwutax.playback.replay.render.RenderManager;
import kaptainwutax.playback.replay.render.ReplayCamera;
import kaptainwutax.playback.util.Quaternion;
import kaptainwutax.playback.util.Vec3f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V"))
    private void rotateRoll(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (camera instanceof ReplayCamera) {
            Quaternion old = Vec3f.POSITIVE_Z.getDegreesQuaternion(((ReplayCamera) camera).getRoll());
            matrix.multiply(new Quaternionf(old.getX(), old.getY(), old.getZ(), old.getW()));
        }
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("HEAD"), cancellable = true)
    private void replayCameraMatrix(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (camera instanceof ReplayCamera) {
            cir.setReturnValue(((ReplayCamera)camera).getBasicProjectionMatrix().toJOML());
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

    //The following 2 redirects cause the replayed screen (size is replayed) to get stretched to the window the user sees during the replaying
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferWidth()I", ordinal = 1))
    public int getReplayedFrameBufferWidth(Window window) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                return (size.getFramebufferWidth());
        }
        return window.getFramebufferWidth();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getFramebufferHeight()I", ordinal = 1))
    public int getReplayedFrameBufferHeight(Window window) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                return (size.getFramebufferHeight());
        }
        return window.getFramebufferHeight();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;viewport(IIII)V"))
    private void fixViewport(int x, int y, int width, int height) {
        if (Playback.getManager().renderManager.isRendering()) return;
        RenderSystem.viewport(x, y, width, height);
    }
}
