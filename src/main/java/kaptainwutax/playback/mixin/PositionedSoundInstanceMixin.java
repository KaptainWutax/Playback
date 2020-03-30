package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PositionedSoundInstance.class)
public class PositionedSoundInstanceMixin {

	@Inject(method = "master(Lnet/minecraft/sound/SoundEvent;FF)Lnet/minecraft/client/sound/PositionedSoundInstance;", at = @At("HEAD"), cancellable = true)
	private static void master(SoundEvent sound, float volume, float pitch, CallbackInfoReturnable<PositionedSoundInstance> ci) {
		if(Playback.isProcessingReplay && Playback.manager.getView() == ReplayView.THIRD_PERSON) {
			float newValues = 0.0F;

			ci.setReturnValue(new PositionedSoundInstance(sound.getId(), SoundCategory.MASTER, newValues, newValues, false,
					0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true));
		}
	}

}
