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
		return new PlayNetworkHandler(client.getNetworkHandler());
	}

	public void apply() {
		INetworkHandlerCaller current = (INetworkHandlerCaller)client.getNetworkHandler();
		current.setAdvancementHandler(this.internal.getAdvancementHandler());
		current.setCommandSource(this.internal.getCommandSource());
		current.setTagManager(this.internal.getTagManager());
		current.setDataQueryManager(this.internal.getDataQueryManager());
		current.setChunkLoadDistance(this.internal.getChunkLoadDistance());
		current.setCommandDispatcher(this.internal.getCommandDispatcher());
		current.setRecipeManager(this.internal.getRecipeManager());
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