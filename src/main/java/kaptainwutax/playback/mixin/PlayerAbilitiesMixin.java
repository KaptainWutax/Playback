package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAbilities.class)
public class PlayerAbilitiesMixin {

	@Inject(method = "setFlySpeed", at = @At("HEAD"))
	private void setFlySpeed(float flySpeed, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickInfo().recordSetFlySpeed(flySpeed);
		}
	}

}
