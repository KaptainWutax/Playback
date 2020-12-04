package kaptainwutax.playback.replay.capture;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.tag.TagManager;

public class PlayNetworkHandler {

	private static final MinecraftClient client = MinecraftClient.getInstance();
	private final INetworkHandlerCaller internal;

	private PlayNetworkHandler(ClientPlayNetworkHandler internal) {
		this.internal = (INetworkHandlerCaller)internal;
	}

	public static PlayNetworkHandler createNew() {
		return new PlayNetworkHandler(
				new ClientPlayNetworkHandler(client, null,
						((PacketAction.IConnectionGetter)MinecraftClient.getInstance()).getConnection(),
						client.getSession().getProfile())
		);
	}

	public static PlayNetworkHandler createFromExisting() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
		ClientPlayNetworkHandler internal = new ClientPlayNetworkHandler(client, null, null, null);
		assert clientPlayNetworkHandler != null;
		//noinspection ConstantConditions
		copyContents((INetworkHandlerCaller) clientPlayNetworkHandler, (INetworkHandlerCaller) internal);
		return new PlayNetworkHandler(internal);
	}

	//We are not swapping the NetworkHandler Objects themselves, because there are many references to it,
	//some of which are created without synchronization on other threads in forceMainThread
	public void apply() {
		INetworkHandlerCaller current = (INetworkHandlerCaller)client.getNetworkHandler();
		assert current != null;
		copyContents(this.internal, current);
	}

	public static void copyContents(INetworkHandlerCaller from, INetworkHandlerCaller to) {
		to.setAdvancementHandler(from.getAdvancementHandler());
		to.setCommandSource(from.getCommandSource());
		to.setTagManager(from.getTagManager());
		to.setDataQueryManager(from.getDataQueryManager());
		to.setChunkLoadDistance(from.getChunkLoadDistance());
		to.setCommandDispatcher(from.getCommandDispatcher());
		to.setRecipeManager(from.getRecipeManager());
	}

	public void copyState() {
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		assert networkHandler != null;
		copyContents((INetworkHandlerCaller) networkHandler, this.internal);
	}

	public interface INetworkHandlerCaller {
		ClientAdvancementManager getAdvancementHandler();
		ClientCommandSource getCommandSource();
		TagManager getTagManager();
		DataQueryHandler getDataQueryManager();
		int getChunkLoadDistance();
		CommandDispatcher<CommandSource> getCommandDispatcher();
		RecipeManager getRecipeManager();

		void setAdvancementHandler(ClientAdvancementManager advancementHandler);
		void setCommandSource(ClientCommandSource commandSource);
		void setTagManager(TagManager tagManager);
		void setDataQueryManager(DataQueryHandler dataQueryManager);
		void setChunkLoadDistance(int chunkLoadDistance);
		void setCommandDispatcher(CommandDispatcher<CommandSource> commandDispatcher);
		void setRecipeManager(RecipeManager recipeManager);
	}

}
