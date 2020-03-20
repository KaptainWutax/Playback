package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(boolean bl, CallbackInfo ci) {
		/*
		if (Playback.isReplaying) {
			if(Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null
					&& (Object)this == Playback.manager.replayPlayer.getPlayer().input) {

				//get the input action from the future to make up for the delay
				if(Playback.recording.getNextTickCapture().third.input != null) {
					Playback.recording.getNextTickCapture().third.input.play((KeyboardInput)(Object)this);
				}

				ci.cancel();
			}
		} */

	}
	@Inject(method = "tick", at = @At("TAIL"))
	//take info at TAIL, so we are not outdated
	//taking info at HEAD is bad, because the info is changed right after, and we probably want the new one
	private void tickEnd(boolean bl, CallbackInfo ci) {
		/*
		if(Playback.recording.isRecording()) {
			Playback.recording.getCurrentTickCapture().recordInputAction((KeyboardInput)(Object)this);
		}*/
	}
}
