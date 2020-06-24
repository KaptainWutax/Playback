package kaptainwutax.playback.mixin.client.util;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import kaptainwutax.playback.replay.ReplayManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class that allows recording and replaying window size, scaled size and scale factor. This is required to make replayed screens work
 * as they have to be the exact same size internally when recording and replaying, to work around rounding errors in mouse coordinates
 * that could lead to clicks not hitting a button, setting a wrong slider value or clicking a wrong inventory slot.
 */

@Mixin(Window.class)
public class WindowMixin implements ReplayManager.IWindowCaller {
    @Shadow private int width;

    @Shadow private int height;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow private double scaleFactor;

    @Shadow private int framebufferWidth;

    @Shadow private int framebufferHeight;

    public WindowSize getWindowSize() {
        assert Playback.getManager().isRecording();
        return new WindowSize(this.width, this.height, this.scaledWidth, this.scaledHeight, this.scaleFactor, this.framebufferWidth, this.framebufferHeight);
    }

    public void recordWindowSize(boolean runOnResolutionChanged) {
        if (Playback.getManager().isRecording()) {
            Playback.getManager().recording.getCurrentTickInfo().recordWindowSize(
                    this.getWindowSize(), runOnResolutionChanged
            );
        }
    }

    @Inject(method = "setScaleFactor", at = @At("RETURN"))
    public void recordNewSize1(double scaleFactor, CallbackInfo ci) {
        this.recordWindowSize(false);
    }

    @Inject(method = "onFramebufferSizeChanged", at = @At("RETURN"))
    public void recordNewSize2(long window, int width, int height, CallbackInfo ci) {
        this.recordWindowSize(true);
    }

    @Inject(method = "updateWindowRegion", at = @At("RETURN"))
    public void recordNewSize3(CallbackInfo ci) {
        this.recordWindowSize(false);
    }

    @Inject(method = "onWindowSizeChanged", at = @At("RETURN"))
    public void recordNewSize4(long window, int width, int height, CallbackInfo ci) {
        this.recordWindowSize(false);
    }


    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
    public void getHeight(CallbackInfoReturnable<Integer> cir) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                cir.setReturnValue(size.getHeight());
        }
    }

    @Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
    public void getWidth(CallbackInfoReturnable<Integer> cir) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                cir.setReturnValue(size.getWidth());
        }
    }

    @Inject(method = "getScaledHeight", at = @At("HEAD"), cancellable = true)
    public void getScaledHeight(CallbackInfoReturnable<Integer> cir) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                cir.setReturnValue(size.getScaledHeight());
        }
    }

    @Inject(method = "getScaledWidth", at = @At("HEAD"), cancellable = true)
    public void getScaledWidth(CallbackInfoReturnable<Integer> cir) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                cir.setReturnValue(size.getScaledWidth());
        }
    }

    @Inject(method = "getScaleFactor", at = @At("HEAD"), cancellable = true)
    public void getScaleFactor(CallbackInfoReturnable<Double> cir) {
        if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            WindowSize size = Playback.getManager().recording.getCurrentRecordedWindowSize();
            if (size != null)
                cir.setReturnValue(size.getScaleFactor());
        }
    }
}
