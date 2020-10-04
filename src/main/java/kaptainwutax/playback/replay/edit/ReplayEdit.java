package kaptainwutax.playback.replay.edit;

import com.mojang.serialization.Dynamic;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.render.RenderQueue;
import kaptainwutax.playback.render.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReplayEdit implements Renderer {

	protected List<WorldEdit> edits = new ArrayList<>();

	public ReplayEdit() {
		this.load();
	}

	public ReplayEdit(Dynamic<?> config) {

	}

	public void load() {
		RenderQueue.get().add(this);
	}

	public void unload() {
		RenderQueue.get().remove(this);
	}

	public void tick() {
		this.edits.forEach(edit -> edit.setHovered(false));

		WorldEdit closestEdit = null;
		double closestDistance = Double.MAX_VALUE;

		for(WorldEdit edit: this.edits) {
			if(edit.getClickBox().contains(Playback.getManager().cameraPlayer.getPlayer().getCameraPosVec(1.0F))) {
				closestEdit = edit;
				break;
			}

			Optional<Vec3d> r = edit.rayTrace(Playback.getManager().cameraPlayer.getPlayer());

			if(r.isPresent()) {
				double distance = r.get().distanceTo(Playback.getManager().cameraPlayer.getPlayer().getPos());

				if(distance < closestDistance) {
					closestEdit = edit;
				}
			}
		}

		if(closestEdit != null) {
			closestEdit.setHovered(true);
		}
	}

	@Override
	public void render(float tickDelta, MatrixStack matrices) {
		for(WorldEdit edit: this.edits) {
			if(edit.shouldRender(MinecraftClient.getInstance().gameRenderer.getCamera())) {
				edit.render(tickDelta, matrices);
			}
		}
	}

	public void add(WorldEdit edit) {
		this.edits.add(edit);
	}

	@Override
	public BlockPos getCenter() {
		return BlockPos.ORIGIN;
	}

	@Override
	public boolean shouldRender(Camera camera) {
		return true; //Always render.
	}

}
