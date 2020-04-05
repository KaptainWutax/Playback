package kaptainwutax.playback.mixin.client.util;

import kaptainwutax.playback.Playback;
import net.minecraft.client.util.Clipboard;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;

@Mixin(Clipboard.class)
public class ClipboardMixin {

    @Inject(method = "getClipboard", at = @At(value = "HEAD"), cancellable = true)
    private void getClipboardFromRecording(long window, GLFWErrorCallbackI gLFWErrorCallbackI, CallbackInfoReturnable<String> cir) {
        if (Playback.getManager().isReplaying() && Playback.getManager().isProcessingReplay)
            cir.setReturnValue(Playback.getManager().recording.getClipboardNow());
    }

    @Inject(method = "getClipboard", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void recordClipboard(long window, GLFWErrorCallbackI gLFWErrorCallbackI, CallbackInfoReturnable<String> cir, GLFWErrorCallback gLFWErrorCallback, String string) {
        if (Playback.getManager().isRecording())
            Playback.getManager().recording.getCurrentTickInfo().recordClipboard(string);
    }

    @Redirect(method = "setClipboard(JLjava/nio/ByteBuffer;[B)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetClipboardString(JLjava/nio/ByteBuffer;)V"))
    private static void doNotSetClipboardIfReplayed(long window, ByteBuffer string) {
        if (!Playback.getManager().isProcessingReplay) {
            GLFW.glfwSetClipboardString(window, string);
        }
    }
}
