package kaptainwutax.playback.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderQueue {

	private static RenderQueue INSTANCE = new RenderQueue();

	private List<Renderer> renderers = new ArrayList<>();

	public RenderQueue() {
		Line line = new Line(new Vec3d(0, 0, 0), new Vec3d(100, 100, 100));
		Text3D text = new Text3D("Hello World", new BlockPos(100, 100, 100));
		this.add(line, text);
	}

	public void add(Renderer... renderers) {
		this.renderers.addAll(Arrays.asList(renderers));
	}

	public void remove(Renderer... renderers) {
		for(Renderer renderer: renderers) {
			this.renderers.remove(renderer);
		}
	}

	public void render(float tickDelta, MatrixStack matrices) {
		this.renderers.forEach(r -> Renderer.render(r, tickDelta, matrices));
	}

	public static RenderQueue get() {
		return INSTANCE;
	}

}
