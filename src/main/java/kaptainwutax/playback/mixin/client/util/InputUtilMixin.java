package kaptainwutax.playback.mixin.client.util;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
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
		if(Playback.getManager().isRecording() && GLFW.glfwGetKey(handle, i) == 1) {
			Playback.getManager().recording.getCurrentTickInfo().recordKeyState(i);
		} else if(Playback.getManager().isReplaying() && Playback.getManager().getView() == ReplayView.FIRST_PERSON && Playback.getManager().isProcessingReplay) {
			ci.setReturnValue(Playback.getManager().recording.getCurrentTickInfo().getKeyState(i));
		}
	}
	
}
