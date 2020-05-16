package kaptainwutax.playback.replay.edit;

import kaptainwutax.playback.render.Cuboid;
import kaptainwutax.playback.render.Renderer;
import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public abstract class WorldEdit implements Renderer {

	private Box clickBox;
	private Color boxColor;
	protected Cuboid outline;

	private boolean hovered;
	private boolean clicked;
	private boolean dirty = true;

	public WorldEdit(Box clickBox, Color boxColor) {
		this.clickBox = clickBox;
		this.boxColor = boxColor;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public boolean isHovered() {
		return this.hovered;
	}

	public boolean isClicked() {
		return this.clicked;
	}

	public Box getClickBox() {
		return this.clickBox;
	}

	public void setDirty() {
		this.dirty = true;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public void setClickBox(Box clickBox) {
		this.clickBox = clickBox;
		this.setDirty();
	}

	public void setBoxColor(Color boxColor) {
		this.boxColor = boxColor;
		this.setDirty();
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		if(this.dirty) {
			this.refresh();
			this.dirty = false;
		}

		if(this.isHovered()) {
			this.outline.render(tickDelta, matrices);
		}
	}

	public void tick(Camera camera) {

	}

	public Optional<Vec3d> rayTrace(Entity entity) {
		double d = MinecraftClient.getInstance().options.viewDistance * 16.0D;
		Vec3d vec3d = entity.getCameraPosVec(1.0F);
		Vec3d vec3d2 = entity.getRotationVec(1.0F);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.clickBox.rayTrace(entity.getCameraPosVec(1.0F), vec3d3);
	}

	public void refresh() {
		this.outline = new Cuboid(this.clickBox, this.boxColor);
	}

}
