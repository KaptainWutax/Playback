package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.replay.render.RenderManager;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin implements RenderManager.ISetForcedFrameRate {
    @Shadow public float lastFrameDuration; //duration measured in gameticks
    @Shadow public float tickDelta; //tick delta between 0 and 1 (0 means prev tick is used, 1 means current tick is used, linear interpolation)
    @Shadow private long prevTimeMillis;

    @Mutable
    @Shadow @Final private float tickTime;
    private boolean useFixedFramerate;
    private float fixedTicksPerFrame;

    public void setFixedFrameRateForVideoRender(float framesPerTick) {
        this.fixedTicksPerFrame = 1F / framesPerTick;
    }

    public void setFixedFrameRateForVideoRenderEnabled(boolean enabled) {
        this.useFixedFramerate = enabled;
    }

    @Inject(method = "beginRenderTick", at = @At("HEAD"), cancellable = true)
    private void adjustForFixedFramerate(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        if(this.useFixedFramerate) { //Video rendering.
            this.lastFrameDuration = this.fixedTicksPerFrame;
            this.prevTimeMillis = timeMillis;
            this.tickDelta += this.lastFrameDuration;
            int ticksThisFrame = (int) this.tickDelta;
            this.tickDelta -= (float) ticksThisFrame;
            ci.cancel();
        }
    }
}
