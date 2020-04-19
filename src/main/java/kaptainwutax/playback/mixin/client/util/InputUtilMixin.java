package kaptainwutax.playback.mixin.client.util;

import kaptainwutax.playback.Playback;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public abstract class InputUtilMixin {

	@Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void isKeyPressed(long handle, int i, CallbackInfoReturnable<Boolean> ci) {
		if (Playback.getManager().isRecording()) {
			int pressedState = GLFW.glfwGetKey(handle, i);
			Playback.getManager().recording.setKeyState(i, pressedState == GLFW.GLFW_PRESS);
			ci.setReturnValue(pressedState == GLFW.GLFW_PRESS);
		} else if (Playback.getManager().isInReplay() && Playback.getManager().isOnlyAcceptingReplayedInputs()) {
			boolean b = Playback.getManager().recording.getKeyState(i);
			ci.setReturnValue(b);
		}
	}
	
}
