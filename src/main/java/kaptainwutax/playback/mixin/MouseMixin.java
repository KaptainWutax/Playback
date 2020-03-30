package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.action.IMouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin implements IMouse {

	private boolean replayingAction;
	private boolean windowFocusOverride;
	private boolean originalIsCursorLocked;


	@Shadow
	protected abstract void onCursorPos(long window, double x, double y);

	@Shadow
	protected abstract void onMouseButton(long window, int button, int action, int mods);

	@Shadow
	protected abstract void onMouseScroll(long window, double d, double e);

	@Shadow
	public abstract void updateMouse();

	@Shadow private boolean isCursorLocked;

	@Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
	private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordMouse(0, x, y, 0, this.isCursorLocked);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordMouse(1, button, action, mods, this.isCursorLocked);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void onMouseScroll(long window, double d, double e, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordMouse(2, d, e, 0, this.isCursorLocked);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	@Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
	private void updateMouse(CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordMouse(3, 0, 0, 0, this.isCursorLocked);
		} else if(!Playback.allowInput) {
			ci.cancel();
		}
	}

	/**
	 * Apply the recorded window focus, to make mouse actions be interpreted like in the recording
	 * Fix tabbing out causing wrong rotation in 1st person replay due to mouse event being interpreted depending on window focus.
	 * @param minecraftClient the minecraft client
	 * @return whether the window is focused, the recorded value when replaying an event at the moment, otherwise actual value
	 */
	@Redirect(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isWindowFocused()Z"))
	private boolean isWindowFocusOverride(MinecraftClient minecraftClient) {
		if (this.replayingAction) {
			return this.windowFocusOverride;
		}
		return minecraftClient.isWindowFocused();
	}
	@Redirect(method = "onCursorPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isWindowFocused()Z"))
	private boolean isWindowFocusOverride2(MinecraftClient minecraftClient) {
		return isWindowFocusOverride(minecraftClient);
	}
	@Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isWindowFocused()Z"))
	private boolean isWindowFocusOverride3(MinecraftClient minecraftClient) {
		return isWindowFocusOverride(minecraftClient);
	}

	@Override
	public void execute(int action, double d1, double d2, int i1, boolean windowFocused, boolean cursorLocked) {
		this.windowFocusOverride = windowFocused; //necessary to fix tabbing out causing wrong rotation in first person
		//the following is probably no longer neccessary
		this.originalIsCursorLocked = this.isCursorLocked;
		this.isCursorLocked = cursorLocked; //replay cursor locked, possibly not necessary to fix tabbing out
		//
		this.replayingAction = true;

		if(action == 0) {
			this.onCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if(action == 1) {
			this.onMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), (int) d1, (int) d2, i1);
		} else if(action == 2) {
			this.onMouseScroll(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if(action == 3) {
			this.updateMouse();
		}

		this.isCursorLocked = this.originalIsCursorLocked;
		this.replayingAction = false;
	}

}
