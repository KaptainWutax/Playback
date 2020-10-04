package kaptainwutax.playback.entity;

import kaptainwutax.playback.init.PItems;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;

public class FakePlayer extends ClientPlayerEntity {

	private ClientPlayerInteractionManager interactionManager;

	//This is the player that carries the camera in THIRD PERSON replay. This should act like a freecam, without influencing the replay
	public FakePlayer(MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler, ClientPlayerInteractionManager interactionManager, PlayGameOptions options, Input keyboardInput) {
		super(client, clientWorld, clientPlayNetworkHandler, null, new ClientRecipeBook(), false, false);

		GameMode gameMode = GameMode.SPECTATOR;

		gameMode.setAbilities(this.abilities);
		this.interactionManager = interactionManager;
		((IInteractionCaller) interactionManager).setGameModeNoUpdates(gameMode);

		this.setClientPermissionLevel(2);
		this.input = keyboardInput;
		//this.dimension = null; TODO: check if this breaks anything

		this.inventory.addPickBlock(new ItemStack(PItems.FEATURE_STICK));
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

	@Override
	public boolean isCreative() {
		return this.interactionManager.getCurrentGameMode() == GameMode.CREATIVE;
	}

	@Override
	public boolean isSpectator() {
		return this.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR;
	}


	public interface IClientCaller {

		void fakeHandleInputEvents();

	}

	public interface IInteractionCaller {

		void setGameModeNoUpdates(GameMode gameMode);

	}


}
