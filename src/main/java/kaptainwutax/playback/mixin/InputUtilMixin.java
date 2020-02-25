package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
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
			Playback.recording.addKeyState(handle, i);
		} else if(Playback.isReplaying) {
			ci.setReturnValue(Playback.recording.getKeyState(Playback.tickCounter, handle, i));
		}
	}

}
