package kaptainwutax.playback.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Line implements Renderer {

	private final Vec3d start;
	private final Vec3d end;
	private final BlockPos center;

	public Line(Vec3d start, Vec3d end) {
		this.start = start;
		this.end = end;

		this.center = new BlockPos(
				start.getX() + (end.getX() - start.getX()) / 2.0D,
				start.getY() + (end.getY() - start.getY()) / 2.0D,
				start.getZ() + (end.getZ() - start.getZ()) / 2.0D
		);
	}

	@Override
	public BlockPos getCenter() {
		return this.center;
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		matrices.push();
		GlStateManager.disableDepthTest();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		//This is how thick the line is.
		GlStateManager.lineWidth(2.0f);
		buffer.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);

		//Put the start and end vertices in the buffer.
		this.putVertex(buffer, matrices.peek().getModel(), this.start);
		this.putVertex(buffer, matrices.peek().getModel(), this.end);

		//Draw it all.
		tessellator.draw();
		matrices.pop();
	}

	protected void putVertex(BufferBuilder buffer, Matrix4f matrix, Vec3d pos) {
		buffer.vertex(matrix,
				(float)pos.getX(),
				(float)pos.getY(),
				(float)pos.getZ()
		).color(
				1.0F,
				1.0F,
				1.0F,
				1.0F
		).next();
	}

}
