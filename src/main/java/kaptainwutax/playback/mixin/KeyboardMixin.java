package kaptainwutax.playback.mixin;

import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.capture.action.IKeyboard;
import kaptainwutax.playback.Playback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements IKeyboard {

	@Shadow public abstract void onKey(long window, int key, int scancode, int i, int j);

	@Shadow protected abstract void onChar(long window, int i, int j);

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null)return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKey(0, window, key, scancode, i, j);
		} else if(Playback.manager.getView() == ReplayView.FIRST_PERSON && !Playback.allowInputs) {
			ci.cancel();
		}
	}

	@Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
	private void onChar(long window, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null)return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordKey(1, window, 0, 0, i, j);
		} else if(Playback.manager.getView() == ReplayView.FIRST_PERSON && !Playback.allowInputs) {
			ci.cancel();
		}
	}

	@Override
	public void execute(int action, long window, int key, int scancode, int i, int j) {
		if(action == 0) {
			this.onKey(window, key, scancode, i, j);
		} else if(action == 1) {
			this.onChar(window, i, j);
		}
	}

}
