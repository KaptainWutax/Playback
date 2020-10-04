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
	 * @param worldChunk the chunk
	 * @param entity the entity to be added
	 */
	@Redirect(method = "checkEntityChunkPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;addEntity(Lnet/minecraft/entity/Entity;)V"))
	private void addEntityIfNotFakePlayer(WorldChunk worldChunk, Entity entity) {
		if (entity instanceof FakePlayer) {
			entity.updateNeeded = true;
			entity.chunkX = MathHelper.floor(entity.getX() / 16D);
			entity.chunkY = MathHelper.floor(entity.getY() / 16D);
			entity.chunkZ = MathHelper.floor(entity.getZ() / 16D);
			if (entity.chunkY >= worldChunk.getEntitySectionArray().length) {
				entity.chunkY = worldChunk.getEntitySectionArray().length - 1;
			} else if (entity.chunkY < 0) {
				entity.chunkY = 0;
			}

			return;
		}
		worldChunk.addEntity(entity);
	}

}
