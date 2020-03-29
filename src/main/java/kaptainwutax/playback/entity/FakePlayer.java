package kaptainwutax.playback.entity;

import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameMode;

public class FakePlayer extends ClientPlayerEntity {
	private boolean spawned;

	//This is the player that carries the camera in THIRD PERSON replay. This should act like a freecam, without influencing the replay
	public FakePlayer(MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler, ClientPlayerInteractionManager interactionManager, PlayGameOptions options) {
		super(client, clientWorld, clientPlayNetworkHandler, null, new ClientRecipeBook(clientWorld.getRecipeManager()));

		GameMode gameMode = GameMode.CREATIVE;

		gameMode.setAbilitites(this.abilities);
		((IInteractionCaller) interactionManager).setGameModeNoUpdates(gameMode);
		this.setGameMode(gameMode);

		this.input = new KeyboardInput(options);
		this.dimension = null;
	}

	@Override
	public void tick() {
		MinecraftClient.getInstance().gameRenderer.updateTargetedEntity(1.0F);
		((IClientCaller) MinecraftClient.getInstance()).fakeHandleInputEvents();
		super.tick();
	}

	@Override
	protected void pushAway(Entity entity) {
		//Don't push away any entity.
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	public interface IClientCaller {

		void fakeHandleInputEvents();

	}

	public interface IInteractionCaller {

		void setGameModeNoUpdates(GameMode gameMode);

	}


}
