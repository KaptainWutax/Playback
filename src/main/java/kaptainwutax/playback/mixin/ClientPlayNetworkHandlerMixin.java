package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(method = "onGameJoin", at = @At(value="INVOKE", target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if(Playback.isReplaying && !Playback.joined) {
			Playback.joined = true;
			Playback.recording.joinPacket.play();
			ci.cancel();
		}
	}

	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
		if(Playback.isReplaying) {
			Playback.manager.cameraPlayer = null;
			Playback.manager.replayPlayer = null;
			Playback.manager.updateView(Playback.manager.getView());
		}
	}

	@Inject(method = "onPlayerPositionLook", at = @At(value="INVOKE", target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",shift = At.Shift.AFTER))
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
		if(!Playback.isReplaying || Playback.manager.getView() != ReplayView.THIRD_PERSON || Playback.manager.cameraPlayer == null)return;
		FakePlayer cameraPlayer = (FakePlayer)Playback.manager.cameraPlayer.getPlayer();
		cameraPlayer.updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
	}

}
