package kaptainwutax.playback.mixin.client.network;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.recording.Recording;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	private boolean initialTeleport;

	@Shadow
	private MinecraftClient client;

	@Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		if (Playback.getManager().isRecording()) {
			try {
				Playback.getManager().recording = new Recording(Playback.getNewRecordingFile(), "rw");
				Playback.getManager().recording.recordJoinPacket(packet);
				Playback.getManager().recording.recordPerspective(MinecraftClient.getInstance().options.perspective);
				Playback.getManager().recording.recordPhysicalSide(MinecraftClient.getInstance().isInSingleplayer());
				Playback.getManager().recording.recordInitialWindowFocus(MinecraftClient.getInstance().isWindowFocused());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(!Playback.getManager().joined) {
			Playback.getManager().joined = true;
			Playback.getManager().recording.getStartStateAction().play();
			this.client.openScreen(null);
			ci.cancel();
		}
	}

	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
		if(Playback.getManager().isReplaying()) {
			Playback.getManager().cameraPlayer = null;
			Playback.getManager().replayPlayer = null;
			Playback.getManager().updateView(Playback.getManager().getView());

			if(Playback.getManager().isProcessingReplay) {
				Playback.getManager().replayPlayer.apply();
			}
		}
	}

	@Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
		if(Playback.getManager().isRecording() || this.initialTeleport ||Playback.getManager().getView() != ReplayView.THIRD_PERSON
				|| Playback.getManager().cameraPlayer == null || Playback.getManager().cameraPlayer.isActive()) return;

		this.initialTeleport = true;
		FakePlayer cameraPlayer = (FakePlayer) Playback.getManager().cameraPlayer.getPlayer();
		PlayerEntity replayPlayer = Playback.getManager().replayPlayer.getPlayer();

		cameraPlayer.updatePositionAndAngles(replayPlayer.getX(), replayPlayer.getY(), replayPlayer.getZ(), replayPlayer.yaw, replayPlayer.pitch);
	}

}
