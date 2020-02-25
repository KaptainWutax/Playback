package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkTicketManager.class)
public class ChunkTicketManagerMixin {

	/**
	 * Disables server chunk loading in replay worlds.
	 * */
	@Inject(method = "addTicket(JLnet/minecraft/server/world/ChunkTicket;)V", at = @At("HEAD"), cancellable = true)
	private void addTicket(long position, ChunkTicket<?> chunkTicket, CallbackInfo ci) {
		if(Playback.isReplaying && chunkTicket.getType() != ChunkTicketType.UNKNOWN) {
			ci.cancel();
		}
	}

}
