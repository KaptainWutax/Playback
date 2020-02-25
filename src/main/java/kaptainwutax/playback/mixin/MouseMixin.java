package kaptainwutax.playback.mixin;

import kaptainwutax.playback.IMouse;
import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin implements IMouse {

	@Shadow protected abstract void onCursorPos(long window, double x, double y);

	@Shadow protected abstract void onMouseButton(long window, int button, int action, int mods);

	@Shadow protected abstract void onMouseScroll(long window, double d, double e);

	@Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
	private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null)return;

		if(!Playback.isReplaying) {
			Playback.recording.recordMouse(0, window, x, y, 0);
		} else if(!Playback.allowInputs) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null)return;

		if(!Playback.isReplaying) {
			Playback.recording.recordMouse(1, window, (double)button, (double)action, mods);
		} else if(!Playback.allowInputs) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void onMouseScroll(long window, double d, double e, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null)return;

		if(!Playback.isReplaying) {
			Playback.recording.recordMouse(2, window, d, e, 0);
		} else if(!Playback.allowInputs) {
			ci.cancel();
		}
	}

	@Override
	public void execute(int action, long window, double d1, double d2, int i1) {
		if(action == 0) {
			this.onCursorPos(window, d1, d2);
		} else if(action == 1) {
			this.onMouseButton(window, (int)d1, (int)d2, i1);
		} else if(action == 2) {
			this.onMouseScroll(window, d1, d2);
		}
	}

}
