package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.Recording;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

	@Inject(method = "disconnect", at = @At("HEAD"))
	private void disconnect(CallbackInfo ci) {
		if(!Playback.isReplaying) { //wasRecording
			Playback.recording.setEnd();
		}
		Playback.isReplaying = true;
		if(!Playback.isReplaying) {
			Playback.recording = new Recording(); //experimental, didn't test yet. allows viewing a recording only once, but allows recording again without restart
		}
		//reopening the world will show the replay from the other perspective
		Playback.toggleView();
		Playback.restart();

	}

}
