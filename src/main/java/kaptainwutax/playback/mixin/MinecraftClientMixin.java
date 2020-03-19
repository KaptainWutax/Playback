package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin  {

	@Shadow public ClientWorld world;
	@Shadow public ClientPlayerEntity player;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		if(this.world != null) {
			Playback.update();
		}
	}

	//Intended to fix inconsistency due to differing fps during replay and recording
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
	private int MathMinFixed(int i, int i1){
		return 1;
	}

}
