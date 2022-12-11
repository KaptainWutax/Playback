package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Text3D implements Renderer {

	private final String text;
	private final Vec3d pos;
	private final Color color;

	public Text3D(String text, Vec3d pos, Color color) {
		this.text = text;
		this.pos = pos;
		this.color = color;
	}

	@Override
	public BlockPos getCenter() {
		return new BlockPos(this.pos.x, this.pos.y, this.pos.z);
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		matrices.push();
		matrices.translate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		matrices.scale(1.0F, -1.0F, 1.0F);
		VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();

		float x = -this.getWidth();
		float y = -this.getHeight();
		//this.mc.textRenderer.draw(this.text, x, y, this.color.getRGB(), false, matrices.peek().getModel(),
		//		immediate, true, 0, 15);

		immediate.draw();
		matrices.pop();
	}

	public int getHeight() {
		return this.mc.textRenderer.fontHeight;
	}

	public int getWidth() {
		return this.mc.textRenderer.getWidth(this.text);
	}

}
