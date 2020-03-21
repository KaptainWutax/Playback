package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Unique private boolean tpRanOnce = false;

	/** /todo check if also other things besides entityId have to be fixed
	 * GameJoinS2CPacket is the only troublesome packet in the replay since it recreates the world and player
	 * instance. We need to fix the entityId of the player to have the same entity tick order as during the recording.
	 **/
	/*
	@Redirect(method = "onGameJoin", at = @At(value="INVOKE", target="Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;getEntityId()I"))
	private int fixPlayerEntityId(GameJoinS2CPacket packet) {
		if (!Playback.isReplaying) return packet.getEntityId();
		else return Playback.recording.getPlayerEntityId();
	}*/

	@Inject(method = "onPlayerPositionLook", at = @At(value="INVOKE", target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",shift = At.Shift.AFTER))
	public void onPlayerPositionLookStart(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
		if(Playback.isReplaying && Playback.manager.replayPlayer != null && !this.tpRanOnce) {
			Playback.manager.replayPlayer.getPlayer().updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
			DebugHelper.counterMixinInvoke++;
			this.tpRanOnce = true;
		}

		if(Playback.isReplaying && Playback.manager.cameraPlayer != null) {
			Playback.manager.cameraPlayer.getPlayer().updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
		}
	}

}
