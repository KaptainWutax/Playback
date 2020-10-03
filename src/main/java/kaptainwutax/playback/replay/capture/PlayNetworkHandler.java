package kaptainwutax.playback.replay.capture;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.tag.RegistryTagManager;

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
		current.setRegistryManager(this.internal.getRegistryManager());
		current.setDataQueryManager(this.internal.getDataQueryManager());
		current.setChunkLoadDistance(this.internal.getChunkLoadDistance());
		current.setCommandDispatcher(this.internal.getCommandDispatcher());
		current.setRecipeManager(this.internal.getRecipeManager());
	}

	public interface INetworkHandlerCaller {
		ClientAdvancementManager getAdvancementHandler();
		ClientCommandSource getCommandSource();
		RegistryTagManager getRegistryManager();
		DataQueryHandler getDataQueryManager();
		int getChunkLoadDistance();
		CommandDispatcher<CommandSource> getCommandDispatcher();
		RecipeManager getRecipeManager();

		void setAdvancementHandler(ClientAdvancementManager advancementHandler);
		void setCommandSource(ClientCommandSource commandSource);
		void setRegistryManager(RegistryTagManager registryManager);
		void setDataQueryManager(DataQueryHandler dataQueryManager);
		void setChunkLoadDistance(int chunkLoadDistance);
		void setCommandDispatcher(CommandDispatcher<CommandSource> commandDispatcher);
		void setRecipeManager(RecipeManager recipeManager);
	}

}
