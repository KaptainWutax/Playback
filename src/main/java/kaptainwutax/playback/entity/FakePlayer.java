package kaptainwutax.playback.entity;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.GameMode;

public class FakePlayer extends ClientPlayerEntity {
	private boolean spawned;

	//This is the player that carries the camera in THIRD PERSON replay. This should act like a freecam, without influencing the replay
	public FakePlayer(MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		super(client, clientWorld, clientPlayNetworkHandler, null, null);
		GameMode.SPECTATOR.setAbilitites(this.abilities);
		this.input = new KeyboardInput(client.options);
		this.dimension = null;
	}

	//Actually make the THIRD PERSON player not push around stuff, and also not fall into the void
	//far future to-do: there are some ways in the game that spectators can influence the game, check if that can happen in the replay
	@Override
	public void tick() {
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.cameraPlayer != null) {
			Playback.manager.cameraPlayer.apply();
		}

		((IFakePlayerCaller)MinecraftClient.getInstance()).fakeHandleInputEvents();

		this.abilities.flying = true;
		super.tick();

		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null) {
			Playback.manager.replayPlayer.apply();
		}
	}

	@Override
	public boolean isSpectator() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	public interface IFakePlayerCaller {

		void fakeHandleInputEvents();

	}

}
