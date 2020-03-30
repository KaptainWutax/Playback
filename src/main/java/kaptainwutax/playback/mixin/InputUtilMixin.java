package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayView;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public class InputUtilMixin {

	@Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void isKeyPressed(long handle, int i, CallbackInfoReturnable<Boolean> ci) {
		if(!Playback.isReplaying && GLFW.glfwGetKey(handle, i) == 1) {
			Playback.recording.getCurrentTickInfo().recordKeyState(i);
		} else if(Playback.isReplaying && Playback.manager.getView() == ReplayView.FIRST_PERSON && !KeyBindings.hasKeyCode(i)) {
			ci.setReturnValue(Playback.recording.getCurrentTickInfo().getKeyState(handle, i));
		}
	}

	@Inject(method = "setCursorParameters", at = @At("HEAD"), cancellable = true)
	private static void setCursorParameters(long l, int i, double d, double e, CallbackInfo ci) {
		if(Playback.isProcessingReplay && Playback.manager.getView() == ReplayView.THIRD_PERSON) {
			ci.cancel();
		}
	}

}
