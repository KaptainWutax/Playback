package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@Shadow
	@Final
	private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;

	@Inject(method = "setPressed", at = @At("HEAD"))
	private void setPressed(boolean pressed, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().third.getKeyAction().setKeyPressed((KeyBinding) (Object) this, pressed);
		}
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"))
	private static void onKeyPressed(InputUtil.KeyCode keyCode, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().third.getKeyAction().onKeyPressed(keysByCode.get(keyCode));
		}
	}

	@Inject(method = "reset", at = @At("HEAD"))
	private void reset(CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().third.getKeyAction().reset((KeyBinding) (Object) this);
		}
	}

	@Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
	private void isPressed(CallbackInfoReturnable<Boolean> ci) {
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null && Playback.manager.replayPlayer.isActive()) {
			ci.setReturnValue(Playback.recording.getCurrentTickCapture().third.getKeyAction().getPlayKey((KeyBinding) (Object) this).isPressed());
			return;
		}
	}

	@Inject(method = "wasPressed", at = @At("HEAD"), cancellable = true)
	private void wasPressed(CallbackInfoReturnable<Boolean> ci) {
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null && Playback.manager.replayPlayer.isActive()) {
			ci.setReturnValue(Playback.recording.getCurrentTickCapture().third.getKeyAction().getPlayKey((KeyBinding) (Object) this).wasPressed());
			return;
		} else if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().third.getKeyAction().consumeWasPressed((KeyBinding) (Object) this);
		}
	}

}
