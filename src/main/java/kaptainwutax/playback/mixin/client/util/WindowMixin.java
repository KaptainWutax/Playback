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
public class WindowMixin implements ReplayManager.IGetWindowSize {
    @Shadow private int width;

    @Shadow private int height;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow private double scaleFactor;

    public WindowSize getWindowSize() {
        return new WindowSize(this.width, this.height, this.scaledWidth, this.scaledHeight, this.scaleFactor);
    }

    @Inject(method = "setScaleFactor", at = @At("RETURN"))
    public void recordNewSize(double scaleFactor, CallbackInfo ci) {
        if (Playback.getManager().isRecording()) {
            Playback.getManager().recording.getCurrentTickInfo().recordWindowSize(
                    this.getWindowSize()
            );
        }
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
