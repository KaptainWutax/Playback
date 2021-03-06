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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAction.IMouseCaller {
	@Shadow protected abstract void onCursorPos(long window, double x, double y);
	@Shadow protected abstract void onMouseButton(long window, int button, int action, int mods);
	@Shadow protected abstract void onMouseScroll(long window, double d, double e);
	@Shadow public abstract void updateMouse();

	@Shadow public abstract void onResolutionChanged();

	@Unique private MouseAction latestMouseAction;
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
				this.latestMouseAction = Playback.getManager().recording.getCurrentTickInfo().recordMouse(MouseAction.ActionType.POS, x, y, 0);
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
				this.latestMouseAction = Playback.getManager().recording.getCurrentTickInfo().recordMouse(MouseAction.ActionType.BUTTON, button, action, mods);
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
				this.latestMouseAction = Playback.getManager().recording.getCurrentTickInfo().recordMouse(MouseAction.ActionType.SCROLL, d, e, 0);
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
				this.latestMouseAction = Playback.getManager().recording.getCurrentTickInfo().recordMouse(MouseAction.ActionType.UPDATE, 0, 0, 0);
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

	@Inject(method = "onResolutionChanged", at = @At("HEAD"))
	private void recordResolutionChange(CallbackInfo ci) {
		if(Playback.getManager().isRecording()) {
			if (this.recursionDepth == 0) {
				Playback.getManager().recording.getCurrentTickInfo().recordMouse(MouseAction.ActionType.RESOLUTION_CHANGED, 0, 0, 0);
			} else {
				debug_numNonRecordedRecursiveCalls++;
			}
		}
	}

	@Redirect(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
	private void setCursorParameters1(long l, int i, double d, double e) {
		if (!Playback.getManager().isInReplay()) {
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

	/**
	 * The following 8 ModifyVariable mixins are supposed to record or replay the
	 * default screen size equivalent coordinates of mouse actions.
	 * This is required to correctly replay the mouse movements and actions when screens are open.
	 * @param coord the coordinate
	 * @return unchanged coord if not replaying or the previously recorded coordinate if replaying
	 */
	private double recordOrReplayScreenCoordinate(double coord, int index) {
		if (Playback.getManager().isRecording()) {
			this.latestMouseAction.addScreenPositionData(coord, index);
			return coord;
		} else if (Playback.getManager().isInReplay() && Playback.getManager().isProcessingReplay) {
			if (this.recursionDepth != 1)
				System.out.println("Unexpected recursion depth in mousemixin, probably causes wrong behaviour");
			double retval = this.latestMouseAction.getScreenPositionData(index);

			if (retval != coord)
				//This should never happen, because now the screen size is replayed and the mouse coords are also replayed.
				//This is a preparation to remove the whole recordOrReplayScreenCoordinate again.
				//When removing this we can also add rendering a mouse texture.
				throw new IllegalStateException("Mouse position not replayed correctly!");
			return retval;
		}
		return coord;
	}
	@ModifyVariable(ordinal = 0, method = "onMouseButton"/*, name = "d"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getWidth()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinateButton_d(double d) {
		return recordOrReplayScreenCoordinate(d, 0);
	}
	@ModifyVariable(ordinal = 1, method = "onMouseButton"/*, name = "e"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getHeight()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinateButton_e(double e) {
		return recordOrReplayScreenCoordinate(e, 1);
	}
	@ModifyVariable(ordinal = 3, method = "onMouseScroll"/*, name = "g"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getWidth()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinateScroll_g(double g) {
		return recordOrReplayScreenCoordinate(g, 0);
	}
	@ModifyVariable(ordinal = 4, method = "onMouseScroll"/*, name = "h"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getHeight()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinateScroll_h(double h) {
		return recordOrReplayScreenCoordinate(h, 1);
	}
	@ModifyVariable(ordinal = 2, method = "onCursorPos"/*, name = "d"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getWidth()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinatePos_d(double d) {
		return recordOrReplayScreenCoordinate(d, 0);
	}
	@ModifyVariable(ordinal = 3, method = "onCursorPos"/*, name = "e"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getHeight()I", ordinal = 0, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinatePos_e(double e) {
		return recordOrReplayScreenCoordinate(e, 1);
	}
	@ModifyVariable(ordinal = 4, method = "onCursorPos"/*, name = "f"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getWidth()I", ordinal = 1, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinatePos_f(double f) {
		return recordOrReplayScreenCoordinate(f, 2);
	}
	@ModifyVariable(ordinal = 5, method = "onCursorPos"/*, name = "g"*/, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/Window;getHeight()I", ordinal = 1, shift = At.Shift.BY, by = 4))
	private double recordOrReplayScreenCoordinatePos_g(double g) {
		return recordOrReplayScreenCoordinate(g, 3);
	}




	@Override
	public void execute(MouseAction.ActionType actionType, MouseAction action, double d1, double d2, int i1) {
		this.latestMouseAction = action;
		if (actionType == MouseAction.ActionType.POS) {
			this.onCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if (actionType == MouseAction.ActionType.BUTTON) {
			this.onMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), (int) d1, (int) d2, i1);
		} else if (actionType == MouseAction.ActionType.SCROLL) {
			this.onMouseScroll(MinecraftClient.getInstance().getWindow().getHandle(), d1, d2);
		} else if (actionType == MouseAction.ActionType.UPDATE) {
			this.updateMouse();
		} else if (actionType == MouseAction.ActionType.RESOLUTION_CHANGED) {
			this.onResolutionChanged();
		} else {
			throw new IllegalStateException("Unexpected value: " + actionType);
		}
		this.latestMouseAction = null;
	}

}
