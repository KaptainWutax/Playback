package kaptainwutax.playback.mixin.client.world;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.recording.Recording;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

	@Inject(method = "disconnect", at = @At("HEAD"))
	private void disconnect(CallbackInfo ci) {
		if(!Playback.getManager().isReplaying()) { //wasRecording
			try {
				Playback.getManager().recording.close();
				Playback.getManager().recording = new Recording();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		Playback.getManager().restart();
	}

	/**
	 * Prevent adding fake players to chunks, otherwise getEntities calls can find them, which for example leads to the
	 * replay player's raycasts hitting a fake player or entities pushing themselves out of the fake players which will desync the replay.
	 * @param worldChunk the chunk
	 * @param entity the entity to be added
	 */
	@Redirect(method = "checkChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;addEntity(Lnet/minecraft/entity/Entity;)V"))
	private void addEntityIfNotFakePlayer(WorldChunk worldChunk, Entity entity) {
		if (entity instanceof FakePlayer) {
			entity.updateNeeded = true;
			return;
		}
		worldChunk.addEntity(entity);
	}

}
