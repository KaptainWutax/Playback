package kaptainwutax.playback.mixin.client.network;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.capture.PlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.tag.TagManagerLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements PlayNetworkHandler.INetworkHandlerCaller {

	@Shadow
	private MinecraftClient client;

	@Mutable
	@Shadow @Final private ClientAdvancementManager advancementHandler;

	@Mutable
	@Shadow @Final private ClientCommandSource commandSource;

	@Shadow private TagManagerLoader tagManager;

	@Mutable
	@Shadow @Final private DataQueryHandler dataQueryHandler;

	@Shadow private int chunkLoadDistance;

	@Shadow private CommandDispatcher<CommandSource> commandDispatcher;

	@Mutable
	@Shadow @Final private RecipeManager recipeManager;

	@Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		ReplayManager manager = Playback.getManager();
		if (!manager.isInReplay()) {
			manager.startRecording(packet);
		} else if(!manager.joined) {
			manager.joined = true;
			manager.recording.getStartState().getJoinPacketAction().play();

			ReplayView oldView = manager.getView();
			manager.updateView(ReplayView.FIRST_PERSON, true);
			manager.recording.getStartState().play();
			manager.updateView(oldView, true);

			this.client.openScreen(null);
			ci.cancel();
		}
	}

	/**
	 * Handle the replayed respawn packets. Update the player frames when changing dimensions
	 */
	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
		if(Playback.getManager().isInReplay()) {
			if (Playback.getManager().isProcessingReplay) {
				//Handling the replayed respawn packet
				Playback.getManager().replayPlayer.updatePlayerFrameOnRespawnOrDimensionChange(client.player);
				Playback.getManager().cameraPlayer.updatePlayerFrameOnRespawnOrDimensionChange(client.player);
			}
		}
	}

	/**
	 * Teleport the camera player to the replay player at the beginning of the replay and on respawning.
	 */
	@Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
		if(Playback.getManager().isRecording() || Playback.getManager().getView() != ReplayView.THIRD_PERSON
				|| Playback.getManager().cameraPlayer == null || Playback.getManager().cameraPlayer.wasTeleported ||Playback.getManager().cameraPlayer.isActive()) return;

		Playback.getManager().cameraPlayer.wasTeleported = true;
		FakePlayer cameraPlayer = (FakePlayer) Playback.getManager().cameraPlayer.getPlayer();
		PlayerEntity replayPlayer = Playback.getManager().replayPlayer.getPlayer();

		cameraPlayer.updatePositionAndAngles(replayPlayer.getX(), replayPlayer.getY(), replayPlayer.getZ(), replayPlayer.yaw, replayPlayer.pitch);
	}

	@Override
	public ClientAdvancementManager getAdvancementHandler() {
		return this.advancementHandler;
	}

	@Override
	public ClientCommandSource getCommandSource() {
		return this.commandSource;
	}

	@Override
	public TagManagerLoader getRegistryManager() {
		return this.tagManager;
	}

	@Override
	public DataQueryHandler getDataQueryManager() {
		return this.dataQueryHandler;
	}

	@Override
	public int getChunkLoadDistance() {
		return this.chunkLoadDistance;
	}

	@Override
	public CommandDispatcher<CommandSource> getCommandDispatcher() {
		return this.commandDispatcher;
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	@Override
	public void setAdvancementHandler(ClientAdvancementManager advancementHandler) {
		this.advancementHandler = advancementHandler;
	}

	@Override
	public void setCommandSource(ClientCommandSource commandSource) {
		this.commandSource = commandSource;
	}

	@Override
	public void setRegistryManager(TagManagerLoader registryManager) {
		this.tagManager = registryManager;
	}

	@Override
	public void setDataQueryManager(DataQueryHandler dataQueryManager) {
		this.dataQueryHandler = dataQueryManager;
	}

	@Override
	public void setChunkLoadDistance(int chunkLoadDistance) {
		this.chunkLoadDistance = chunkLoadDistance;
	}

	@Override
	public void setCommandDispatcher(CommandDispatcher<CommandSource> commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}

	@Override
	public void setRecipeManager(RecipeManager recipeManager) {
		this.recipeManager = recipeManager;
	}

}
