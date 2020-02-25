package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Shadow private MinecraftClient client;
	@Unique private boolean ranOnce = false;

	/**
	 * GameJoinS2CPacket is the only troublesome packet in the replay since it recreates the world and player
	 * instance. This allows handling of the second GameJoinS2CPacket packet from the recording.
	 **/
	@Inject(method = "onGameJoin", at = @At(value="INVOKE", target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",shift=At.Shift.AFTER), cancellable = true)
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if(!Playback.isReplaying)return;

		if(this.ranOnce && this.client.player != null) {
			this.client.world.getLevelProperties().loadLevelInfo(new LevelInfo(packet.getSeed(), packet.getGameMode(), false, packet.isHardcore(), packet.getGeneratorType()));
			this.client.player.dimension = packet.getDimension();
			this.client.player.setEntityId(packet.getEntityId());
			this.client.player.setReducedDebugInfo(packet.hasReducedDebugInfo());
			this.client.player.setShowsDeathScreen(packet.showsDeathScreen());
			this.client.interactionManager.setGameMode(packet.getGameMode());
			ci.cancel();
		}

		this.ranOnce = true;
	}

}
