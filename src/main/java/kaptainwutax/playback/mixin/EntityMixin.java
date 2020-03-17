package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public class EntityMixin {

	@Shadow @Final protected Random random;

	@Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordChangeLook(cursorDeltaX, cursorDeltaY);
		}
	}

	@Inject(method = "setPos", at = @At("HEAD"))
	private void trackSetPosCalls(double x, double y, double z, CallbackInfo ci){
		DebugHelper.registerEvent((Entity)(Object)this, x, y, z);
	}

	@Inject(method = "pushAwayFrom", at = @At("HEAD"))
	public void pushAwayFrom(Entity entity, CallbackInfo ci) {
		this.random.setSeed(0L);
	}

}
