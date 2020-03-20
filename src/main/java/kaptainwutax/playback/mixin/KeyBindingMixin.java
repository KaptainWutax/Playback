package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.action.KeyBindingAction;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements KeyBindingAction.IPublicKeys {

	@Shadow @Final private static Map<String, KeyBinding> keysById;
	@Shadow private InputUtil.KeyCode keyCode;

	@Shadow @Final private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;

	@Override
	public InputUtil.KeyCode getKeyCode(String keyId) {
		return ((KeyBindingAction.IPublicKeys)keysById.get(keyId)).getKeyCode();
	}

	@Override
	public InputUtil.KeyCode getKeyCode() {
		return this.keyCode;
	}

	@Inject(method = "setPressed", at = @At("HEAD"))
	private void setPressed(boolean pressed, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKeyBinding(KeyBindingAction.SET_KEY_PRESSED, (KeyBinding)(Object)this, pressed);
		}
	}

	@Inject(method = "unpressAll", at = @At("HEAD"))
	private static void unpressAll(CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKeyBinding(KeyBindingAction.UNPRESS_ALL, null, false);
		}
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"))
	private static void onKeyPressed(InputUtil.KeyCode keyCode, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKeyBinding(KeyBindingAction.ON_KEY_PRESSED, keysByCode.get(keyCode), false);
		}
	}

}
