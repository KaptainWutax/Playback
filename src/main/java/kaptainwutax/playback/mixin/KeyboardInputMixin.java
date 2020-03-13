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
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordInputAction((KeyboardInput)(Object)this);
		} else if(Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null
				&& (Object)this == Playback.manager.replayPlayer.getPlayer().input) {
			if(Playback.recording.getCurrentTickCapture().third.input != null) {
				Playback.recording.getCurrentTickCapture().third.input.play((KeyboardInput)(Object)this);
			}

			ci.cancel();
		}
	}

}
