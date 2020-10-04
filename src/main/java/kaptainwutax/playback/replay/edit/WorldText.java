package kaptainwutax.playback.replay.edit;

import com.mojang.serialization.Dynamic;
import kaptainwutax.playback.render.Text3D;
import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WorldText extends WorldEdit {

	private String text;
	private Vec3d pos;
	private Color color;

	private Text3D text3D;

	public WorldText(String text, Vec3d pos, Color color) {
		super(null, Color.WHITE);
		this.text = text;
		this.pos = pos;
		this.color = color;
	}

	public WorldText(Dynamic<?> config) {
		super(null, Color.WHITE);
	}

	public void setText(String text) {
		this.text = text;
		this.setDirty();
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
		this.setDirty();
	}

	public void setPos(BlockPos pos) {
		this.setPos(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	public void setColor(Color color) {
		this.color = color;
		this.setDirty();
	}

	@Override
	public void refresh() {
		this.text3D = new Text3D(this.text, this.pos, this.color);

		double x = this.pos.x;
		double y = this.pos.y;
		double z = this.pos.z;
		int h = this.text3D.getHeight();
		int w = this.text3D.getWidth();

		this.setClickBox(new Box(x - w, y + 2.0D, z - 0.2D, x - 1.0D, y + h, z + 0.2D));
		super.refresh();
	}

	@Override
	public BlockPos getCenter() {
		return this.text3D == null ? BlockPos.ORIGIN : this.text3D.getCenter();
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		super.render(tickDelta, matrices);

		if(this.text3D != null) {
			this.text3D.render(tickDelta, matrices);
		}
	}

}
