package kaptainwutax.playback.mixin;

import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin implements FakePlayer.IInteractionCaller {

	@Shadow private GameMode gameMode;

	@Override
	public void setGameModeNoUpdates(GameMode gameMode) {
		this.gameMode = gameMode;
	}

}
