package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordChangeLook(cursorDeltaX, cursorDeltaY);
		}
	}

}
