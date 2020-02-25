package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "render", at = @At(value="INVOKE", target="Lnet/minecraft/client/MinecraftClient;tick()V",shift=At.Shift.BEFORE))
	private void render(boolean tick, CallbackInfo ci) {
		Playback.update();
	}

}
