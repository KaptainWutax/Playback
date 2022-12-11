package kaptainwutax.playback.replay.capture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.world.ClientWorld;

public class PlayRenderers {

	private static final MinecraftClient client = MinecraftClient.getInstance();

	private final BufferBuilderStorage bufferBuilders;
	private final DebugRenderer debugRenderer;
	private final WorldRenderer worldRenderer;
	private final GameRenderer gameRenderer;
	private final HeldItemRenderer heldItemRenderer;

	public PlayRenderers(BufferBuilderStorage bufferBuilders, DebugRenderer debugRenderer, WorldRenderer worldRenderer,
	                     GameRenderer gameRenderer, HeldItemRenderer heldItemRenderer) {
		this.bufferBuilders = bufferBuilders;
		this.debugRenderer = debugRenderer;
		this.worldRenderer = worldRenderer;
		this.gameRenderer = gameRenderer;
		this.heldItemRenderer = heldItemRenderer;
	}

	public static PlayRenderers createNew() {
		BufferBuilderStorage bb = new BufferBuilderStorage();
		WorldRenderer worldRenderer = new WorldRenderer(client, client.getEntityRenderDispatcher(), client.getBlockEntityRenderDispatcher(), bb);
		HeldItemRenderer heldItemRenderer = new HeldItemRenderer(client, client.getEntityRenderDispatcher(), client.getItemRenderer());
		GameRenderer gameRenderer = new GameRenderer(client, heldItemRenderer, client.getResourceManager(), bb);
		//((ReloadableResourceManager)client.getResourceManager()).registerListener(worldRenderer);
		//((ReloadableResourceManager)client.getResourceManager()).registerListener(gameRenderer);

		return new PlayRenderers(bb, new DebugRenderer(client), worldRenderer, gameRenderer, heldItemRenderer);
	}

	public void apply() {
		//if(((IWorldRendererCaller)this.worldRenderer).getWorld() !=
		//		((IWorldRendererCaller)client.worldRenderer).getWorld()) {
		//	this.worldRenderer.setWorld(((IWorldRendererCaller)client.worldRenderer).getWorld());
		//}

		//((IClientCaller)client).setBufferBuilders(this.bufferBuilders);
		//((IClientCaller)client).setWorldRenderer(this.worldRenderer);
		//((IClientCaller)client).setGameRenderer(this.gameRenderer);
		((IClientCaller)client).setDebugRenderer(this.debugRenderer);
		//((IClientCaller)client).setHeldItemRenderer(this.heldItemRenderer);
	}

	public static PlayRenderers createFromExisting() {
		return new PlayRenderers(
				client.getBufferBuilders(),
				client.debugRenderer,
				client.worldRenderer,
				client.gameRenderer,
				client.getEntityRenderDispatcher().getHeldItemRenderer());
	}

	public interface IClientCaller {
		void setBufferBuilders(BufferBuilderStorage bufferBuilders);
		void setWorldRenderer(WorldRenderer worldRenderer);
		void setGameRenderer(GameRenderer gameRenderer);
		void setDebugRenderer(DebugRenderer debugRenderer);
		void setHeldItemRenderer(HeldItemRenderer heldItemRenderer);
	}

	public interface IWorldRendererCaller {
		ClientWorld getWorld();
	}

}
