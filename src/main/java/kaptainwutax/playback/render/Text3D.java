package kaptainwutax.playback.render;

import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class Text3D implements Renderer {

	private final String text;
	private final BlockPos pos;
	private final Color color;

	public Text3D(String text, BlockPos pos, Color color) {
		this.text = text;
		this.pos = pos;
		this.color = color;
	}

	@Override
	public BlockPos getCenter() {
		return this.pos;
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		matrices.push();
		matrices.translate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		matrices.scale(1.0F, -1.0F, 1.0F);
		VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
		float s = (float)(-this.mc.textRenderer.getStringWidth(this.text) / 2);
		this.mc.textRenderer.draw(this.text, s, 0, this.color.getRGB(), false, matrices.peek().getModel(),
				immediate, true, 0, 15);
		immediate.draw();
		matrices.pop();
	}

}
