package kaptainwutax.playback.render;

import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderQueue {

	private static RenderQueue INSTANCE = new RenderQueue();

	private List<Renderer> renderers = new ArrayList<>();

	public RenderQueue() {

	}

	public void add(Renderer... renderers) {
		this.renderers.addAll(Arrays.asList(renderers));
	}

	public void remove(Renderer... renderers) {
		for(Renderer renderer: renderers) {
			this.renderers.remove(renderer);
		}
	}

	public void clear() {
		this.renderers.clear();
	}

	public void render(float tickDelta, MatrixStack matrices) {
		this.renderers.forEach(r -> Renderer.render(r, tickDelta, matrices));
	}

	public static RenderQueue get() {
		return INSTANCE;
	}

}
