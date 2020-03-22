package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.action.first.IKeyboard;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements IKeyboard {

	@Shadow
	public abstract void onKey(long window, int key, int scancode, int i, int j);

	@Shadow
	protected abstract void onChar(long window, int i, int j);

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordKey(0, key, scancode, i, j);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	@Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
	private void onChar(long window, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordKey(1, 0, 0, i, j);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	@Override
	public void execute(int action, int key, int scanCode, int i, int j) {
		if(action == 0) {
			this.onKey(MinecraftClient.getInstance().getWindow().getHandle(), key, scanCode, i, j);
		} else if(action == 1) {
			this.onChar(MinecraftClient.getInstance().getWindow().getHandle(), i, j);
		}
	}

}
