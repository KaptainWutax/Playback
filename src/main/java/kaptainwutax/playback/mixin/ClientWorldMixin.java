package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.recording.Recording;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

	@Inject(method = "disconnect", at = @At("HEAD"))
	private void disconnect(CallbackInfo ci) {
		if(!Playback.isReplaying) { //wasRecording
			try {
				Playback.recording.close();
				Playback.recording = new Recording();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Playback.restart();
	}

}
