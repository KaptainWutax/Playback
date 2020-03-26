package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayView;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public class InputUtilMixin {

	@Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void isKeyPressed(long handle, int i, CallbackInfoReturnable<Boolean> ci) {
		if(!Playback.isReplaying && GLFW.glfwGetKey(handle, i) == 1) {
			Playback.recording.getCurrentTickInfo().recordKeyState(handle, i);
		} else if(Playback.isReplaying && Playback.manager.getView() == ReplayView.FIRST_PERSON && !KeyBindings.hasKeyCode(i)) {
			ci.setReturnValue(Playback.recording.getCurrentTickInfo().getKeyState(handle, i));
		}
	}

}
