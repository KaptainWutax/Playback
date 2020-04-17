package kaptainwutax.playback.mixin.client;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.PlayerFrame;
import kaptainwutax.playback.replay.action.MouseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAction.IMouseCaller {
	@Shadow protected abstract void onCursorPos(long window, double x, double y);
	@Shadow protected abstract void onMouseButton(long window, int button, int action, int mods);
	@Shadow protected abstract void onMouseScroll(long window, double d, double e);
	@Shadow public abstract void updateMouse();

	@Unique private int recursionDepth;
	@Unique private int debug_numNonRecordedRecursiveCalls;

	@Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
	private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
		this.recursionDepth++;
		if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
			return;
		} else if (MinecraftClient.getInstance().player == null) {
			return;
		}

		if(Playback.getManager().isRecording()) {
			if (this.recursionDepth == 1) {
				Playback.getManager().recording.getCurrentTickInfo().recordMouse(0, x, y, 0);
			} else {
				debug_numNonRecordedRecursiveCalls++;
			}
		} else if(!Playback.getManager().isProcessingReplay && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            this.recursionDepth--;
            ci.cancel();
		}
	}

	@Inject(method = "onCursorPos", at = @At("RETURN"))
	private void onCursorPosEnd(long window, double x, double y, CallbackInfo ci) {
		this.recursionDepth--;
	}

	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		this.recursionDepth++;
		if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
			return;
		} else if (MinecraftClient.getInstance().player == null) {
			return;
		}

		if(Playback.getManager().isRecording()) {
			if (this.recursionDepth == 1) {
				Playback.getManager().recording.getCurrentTickInfo().recordMouse(1, button, action, mods);
			} else {
				debug_numNonRecordedRecursiveCalls++;
			}
		} else if(!Playback.getManager().isProcessingReplay && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            this.recursionDepth--;
            ci.cancel();
		}
	}

	@Inject(method = "onMouseButton", at = @At("RETURN"))
	private void onMouseButtonEnd(long window, int button, int action, int mods, CallbackInfo ci) {
		this.recursionDepth--;
	}

	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void onMouseScroll(long window, double d, double e, CallbackInfo ci) {
		this.recursionDepth++;
		if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
			return;
		} else if (MinecraftClient.getInstance().player == null) {
			return;
		}

		if(Playback.getManager().isRecording()) {
			if (this.recursionDepth == 1) {
				Playback.getManager().recording.getCurrentTickInfo().recordMouse(2, d, e, 0);
			} else {
				debug_numNonRecordedRecursiveCalls++;
			}
		} else if(!Playback.getManager().isProcessingReplay && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            this.recursionDepth--;
            ci.cancel();
		}
	}

	@Inject(method = "onMouseScroll", at = @At("RETURN"))
	private void onMouseScrollEnd(long window, double d, double e, CallbackInfo ci) {
		this.recursionDepth--;
	}

	@Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
	private void updateMouse(CallbackInfo ci) {
		this.recursionDepth++;
		if(MinecraftClient.getInstance().player == null) return;

		if(Playback.getManager().isRecording()) {
			if (this.recursionDepth == 1) {
				Playback.getManager().recording.getCurrentTickInfo().recordMouse(3, 0, 0, 0);
			} else {
				debug_numNonRecordedRecursiveCalls++;
			}
		} else if(!Playback.getManager().isProcessingReplay && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
            this.recursionDepth--;
            ci.cancel();
		}
	}

	@Inject(method = "updateMouse", at = @At("RETURN"))
	private void updateMouseEnd(CallbackInfo ci) {
		this.recursionDepth--;
	}

	@Redirect(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
	private void setCursorParameters1(long l, int i, double d, double e) {
		if (!Playback.getManager().isReplaying()) {
			InputUtil.setCursorParameters(l, i, d, e);
			return;
		}
		PlayerFrame player = Playback.getManager().getPlayerFrameForView(Playback.getManager().getView());
		if (player == null || player.mouse == (Object)this && Playback.getManager().isCurrentlyAcceptingInputs()) {
			InputUtil.setCursorParameters(l, i, d, e);
		}
	}

	@Redirect(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
	private void setCursorParameters2(long l, int i, double d, double e) {
		this.setCursorParameters1(l, i, d, e);
	}

	@Override
	public void execute(int action, double d1, double d2, int i1) {
		if(action == 0) {
			this.onCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if(action == 1) {
			this.onMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), (int) d1, (int) d2, i1);
		} else if(action == 2) {
			this.onMouseScroll(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if(action == 3) {
			this.updateMouse();
		}
	}

}
