package kaptainwutax.playback.mixin.network;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import kaptainwutax.playback.Playback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

	@Shadow
	@Final
	private NetworkSide side;

	private static Set<Class<? extends Packet<?>>> SEND_WHITELIST = ImmutableSet.of(
			HandshakeC2SPacket.class, LoginHelloC2SPacket.class,
			RequestCommandCompletionsC2SPacket.class, ClientStatusC2SPacket.class
	);

	private static Set<Class<? extends Packet<?>>> RECEIVE_BLACKLIST = ImmutableSet.of(
			ContainerSlotUpdateS2CPacket.class, PlayerAbilitiesS2CPacket.class, HeldItemChangeS2CPacket.class,
			DifficultyS2CPacket.class, CustomPayloadS2CPacket.class, SynchronizeRecipesS2CPacket.class,
			UnlockRecipesS2CPacket.class, PlayerSpawnPositionS2CPacket.class, InventoryS2CPacket.class,
			WorldTimeUpdateS2CPacket.class, ChunkRenderDistanceCenterS2CPacket.class, WorldBorderS2CPacket.class,
			PlayerSpawnPositionS2CPacket.class, PlayerPositionLookS2CPacket.class, PlayerListS2CPacket.class
	);

	/**
	 * In replay worlds, the client can't send any packet it desires. Check the whitelist for a full list of
	 * allowed packets.
	 **/
	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	private void sendImmediately(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
		if(this.side == NetworkSide.CLIENTBOUND && Playback.isReplaying && !SEND_WHITELIST.contains(packet.getClass())) {
			ci.cancel();
		}
	}

	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
		if(this.side == NetworkSide.CLIENTBOUND && Playback.isReplaying && RECEIVE_BLACKLIST.contains(packet.getClass())) {
			ci.cancel();
		}
	}

}
