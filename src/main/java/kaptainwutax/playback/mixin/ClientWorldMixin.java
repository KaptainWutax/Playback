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
        Playback.isReplaying = !Playback.isReplaying;
        if (!Playback.isReplaying) {
            Playback.recording = new Recording(); //experimental, didn't test yet. allows viewing a recording only once, but allows recording again without restart
        }
        Playback.tickCounter = 0;
        Playback.manager.cameraPlayer = null;
        Playback.manager.replayPlayer = null;
    }

}
