package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Unique private boolean tpRanOnce = false;

	@Inject(method = "onGameJoin", at = @At(value="INVOKE", target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if(Playback.isReplaying && !Playback.joined) {
			Playback.joined = true;
			Playback.recording.joinPacket.play();
			ci.cancel();
		}
	}

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
