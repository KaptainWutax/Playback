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

	@Override
	public InputUtil.KeyCode getKeyCode(String keyId) {
		return ((KeyBindingAction.IPublicKeys)keysById.get(keyId)).getKeyCode();
	}

	@Override
	public InputUtil.KeyCode getKeyCode() {
		return this.keyCode;
	}

	@Inject(method = "setPressed", at = @At("HEAD"))
	public void setPressed(boolean pressed, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKeyBinding((KeyBinding)(Object)this, pressed);
		}
	}

}
