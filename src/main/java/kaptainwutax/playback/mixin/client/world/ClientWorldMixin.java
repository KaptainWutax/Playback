package kaptainwutax.playback.mixin.client.world;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

	@Inject(method = "disconnect", at = @At("HEAD"))
	private void disconnect(CallbackInfo ci) {
		Playback.getManager().stopRecording();
		Playback.getManager().restart(null);
	}

	/**
	 * Prevent adding fake players to chunks, otherwise getEntities calls can find them, which for example leads to the
	 * replay player's raycasts hitting a fake player or entities pushing themselves out of the fake players which will desync the replay.
	 * @param entity the entity to be added
	 */
	@Inject(method = "addEntityPrivate", at = @At(value = "HEAD"), cancellable = true)
	private void addEntityIfNotFakePlayer(int id, Entity entity, CallbackInfo ci) {
		if (entity instanceof FakePlayer) {
			ci.cancel();
		}
	}

}
