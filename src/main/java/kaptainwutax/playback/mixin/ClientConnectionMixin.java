package kaptainwutax.playback.mixin;

import com.google.common.collect.ImmutableSet;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import kaptainwutax.playback.Playback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

	@Shadow @Final private NetworkSide side;

	private static Set<Class<? extends Packet<?>>> WHITELIST = ImmutableSet.of(
			DisconnectS2CPacket.class, HandshakeC2SPacket.class, LoginHelloC2SPacket.class,
			RequestCommandCompletionsC2SPacket.class, ClientStatusC2SPacket.class
			//, ChatMessageC2SPacket.class
	);

	/**
	 * In replay worlds, the client can't send any packet it desires. Check the whitelist for a full list of
	 * allowed packets.
	 **/
	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	private void sendImmediately(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
		if(this.side == NetworkSide.CLIENTBOUND && Playback.isReplaying && !WHITELIST.contains(packet.getClass())) {
			ci.cancel();
		}
	}

}
