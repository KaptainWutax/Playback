package kaptainwutax.playback.mixin;

import it.unimi.dsi.fastutil.longs.LongIterator;
import kaptainwutax.playback.Playback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract ServerNetworkIo getNetworkIo();

	@Shadow protected abstract void setLoadingStage(Text loadingStage);

	@Shadow public abstract ServerWorld getWorld(DimensionType dimensionType);

	@Shadow @Final private static Logger LOGGER;

	@Shadow private long timeReference;

	@Shadow protected abstract void method_16208();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if(!Playback.play)return;
		this.getNetworkIo().tick();
		ci.cancel();
	}

	@Inject(method = "prepareStartRegion", at = @At("HEAD"), cancellable = true)
	protected void prepareStartRegion(WorldGenerationProgressListener listener, CallbackInfo ci) {
		if(!Playback.play)return;
		this.setLoadingStage(new TranslatableText("menu.generatingTerrain"));
		ServerWorld serverWorld = this.getWorld(DimensionType.OVERWORLD);
		LOGGER.info("Preparing start region for dimension " + DimensionType.getId(serverWorld.dimension.getType()));
		BlockPos blockPos = serverWorld.getSpawnPos();
		listener.start(new ChunkPos(blockPos));
		ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
		serverChunkManager.getLightingProvider().setTaskBatchSize(500);
		this.timeReference = Util.getMeasuringTimeMs();

		serverChunkManager.addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);

		//while(serverChunkManager.getTotalChunksLoadedCount() != 441) {
		//	this.timeReference = Util.getMeasuringTimeMs() + 10L;
		//	this.method_16208();
		//}

		LOGGER.info("DONE.");

		this.timeReference = Util.getMeasuringTimeMs() + 10L;
		this.method_16208();
		Iterator var5 = DimensionType.getAll().iterator();

		while(true) {
			DimensionType dimensionType;
			ForcedChunkState forcedChunkState;
			do {
				if (!var5.hasNext()) {
					this.timeReference = Util.getMeasuringTimeMs() + 10L;
					this.method_16208();
					listener.stop();
					serverChunkManager.getLightingProvider().setTaskBatchSize(5);
					ci.cancel();
					return;
				}

				dimensionType = (DimensionType)var5.next();
				forcedChunkState = this.getWorld(dimensionType).getPersistentStateManager().get(ForcedChunkState::new, "chunks");
			} while(forcedChunkState == null);

			ServerWorld serverWorld2 = this.getWorld(dimensionType);
			LongIterator longIterator = forcedChunkState.getChunks().iterator();

			while(longIterator.hasNext()) {
				long l = longIterator.nextLong();
				ChunkPos chunkPos = new ChunkPos(l);
				serverWorld2.getChunkManager().setChunkForced(chunkPos, true);
			}
		}
	}

}
