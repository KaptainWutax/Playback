package kaptainwutax.playback.mixin.network;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkThreadUtils.class)
public abstract class NetworkThreadUtilsMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", at = @At("HEAD"))
	private static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ThreadExecutor<?> engine, CallbackInfo ci) {
		if(Playback.getManager().isRecording() && engine.isOnThread() && engine == MinecraftClient.getInstance()) {
			Playback.getManager().recording.getCurrentTickInfo().recordPacket((Packet<ClientPlayPacketListener>) packet);
		}
	}

}
