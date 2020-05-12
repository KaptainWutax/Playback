package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public interface Renderer {

	MinecraftClient mc = MinecraftClient.getInstance();

	BlockPos getCenter();

	void render(float tickDelta, MatrixStack matrices);

	default int getRenderDistance() {
		return MinecraftClient.getInstance().options.viewDistance;
	}

	static void render(Renderer renderer, float tickDelta, MatrixStack matrices) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d camPos = camera.getPos();

		ChunkPos p1 = new ChunkPos(camera.getBlockPos());
		ChunkPos p2 = new ChunkPos(renderer.getCenter());
		int distance = Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.z - p2.z));

		matrices.push();
		matrices.translate(-camPos.getX(), -camPos.getY(), -camPos.getZ());

		if(distance <= renderer.getRenderDistance()) {
			renderer.render(tickDelta, matrices);
		}

		matrices.pop();
	}

	static void putVertex(BufferBuilder buffer, Matrix4f matrix, Vec3d pos, Color color) {
		buffer.vertex(matrix,
				(float)pos.getX(),
				(float)pos.getY(),
				(float)pos.getZ()
		).color(
				color.getFRed(),
				color.getFGreen(),
				color.getFBlue(),
				1.0F
		).next();
	}

}
