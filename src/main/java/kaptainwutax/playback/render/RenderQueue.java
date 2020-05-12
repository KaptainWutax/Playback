package kaptainwutax.playback.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderQueue {

	private static RenderQueue INSTANCE = new RenderQueue();

	private Map<BlockPos, List<Renderer>> renderers = new HashMap<>();

	public RenderQueue() {

	}

	public List<Renderer> getAt(BlockPos pos) {
		return this.renderers.getOrDefault(pos, new ArrayList<>());
	}

	private void addInternal(Renderer renderer) {
		BlockPos pos = renderer.getCenter();
		List<Renderer> list = this.getAt(renderer.getCenter());
		list.add(renderer);
		this.renderers.put(pos, list);
	}

	public void add(Renderer... renderers) {
		for(Renderer renderer: renderers) {
			this.addInternal(renderer);
		}
	}

	public void remove(Renderer... renderers) {
		for(Renderer renderer: renderers) {
			this.getAt(renderer.getCenter()).remove(renderer);
		}
	}

	public void clear() {
		this.renderers.clear();
	}

	public void render(float tickDelta, MatrixStack matrices) {
		this.renderers.values().forEach(list -> list.forEach(r -> Renderer.render(r, tickDelta, matrices)));
	}

	public static RenderQueue get() {
		return INSTANCE;
	}

}
