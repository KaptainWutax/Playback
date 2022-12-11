package kaptainwutax.playback.mixin.client.render;

import com.google.common.collect.Iterables;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.render.RenderQueue;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.capture.PlayRenderers;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements PlayRenderers.IWorldRendererCaller {

	@Shadow private ClientWorld world;
	private Entity tmpEntity;
	private ArrayList<Entity> extraEntities = new ArrayList<>(1);

	@Redirect(method = "render", at = @At(value = "INVOKE",
			target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 0))
	private Object rememberEntity(Iterator iterator) {
		return (tmpEntity = (Entity)(iterator.next()));
	}

	@Redirect(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
	//return a replayPlayer for the replayPlayer, so it can be rendered
	private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera) {
		if(Playback.getManager().isInReplay() && Playback.getManager().getView() == ReplayView.THIRD_PERSON &&
				Playback.getManager().replayPlayer != null && camera.getFocusedEntity() == Playback.getManager().cameraPlayer.getPlayer() &&
				tmpEntity == Playback.getManager().replayPlayer.getPlayer() ) {
			return tmpEntity;
		}

		return camera.getFocusedEntity();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", ordinal = 0))
	private Iterator<Entity> appendCameraPlayerForRender(Iterable<Entity> iterable) {
		if (Playback.getManager().isInReplay() && Playback.getManager().getView() == ReplayView.THIRD_PERSON && Playback.getManager().cameraPlayer != null) {
			if (extraEntities.size() < 1) {
				extraEntities.add(Playback.getManager().cameraPlayer.getPlayer());
			}

			extraEntities.set(0, Playback.getManager().cameraPlayer.getPlayer());
			return Iterables.concat(iterable, extraEntities).iterator();
		} else {
			return iterable.iterator();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void afterRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
		if (!Playback.getManager().isInReplay()) {
			return;
		}
		RenderQueue.get().render(tickDelta, matrices);
		Playback.getManager().renderManager.render(matrices, tickDelta, camera);
	}

	@Override
	public ClientWorld getWorld() {
		return this.world;
	}

}
