package kaptainwutax.playback.mixin.client.network;

import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements FakePlayer.IInteractionCaller, PacketAction.INetworkHandlerGetter {

	@Shadow
	private GameMode gameMode;

	@Shadow @Final private ClientPlayNetworkHandler networkHandler;

	@Override
	public void setGameModeNoUpdates(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	@Override
	public ClientPlayNetworkHandler getNetworkHandler() {
		return this.networkHandler;
	}

}
