package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class RenderQueue {

	private static RenderQueue INSTANCE = new RenderQueue();

	private Map<BlockPos, List<Renderer>> renderers = new HashMap<>();

	public RenderQueue() {
		Line line = new Line(Vec3d.ZERO, new Vec3d(100, 100, -100), Color.GREEN);
		Text3D text = new Text3D("Hello World", new BlockPos(100, 100, -100), new Color(2130706433));
		this.add(line, text);
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
