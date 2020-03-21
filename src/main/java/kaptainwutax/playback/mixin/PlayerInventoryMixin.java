package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	public void scrollInHotbar(double scrollAmount, CallbackInfo ci) {
		if(!Playback.isReplaying) {
			Playback.recording.getCurrentTickCapture().recordScrollInHotbar(scrollAmount);
		}
	}

}
