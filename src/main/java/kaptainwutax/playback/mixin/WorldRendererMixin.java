package kaptainwutax.playback.mixin;

import com.google.common.collect.Iterables;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

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
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON &&
				Playback.manager.replayPlayer != null && camera.getFocusedEntity() == Playback.manager.cameraPlayer.getPlayer() &&
				tmpEntity == Playback.manager.replayPlayer.getPlayer() ) {
			return tmpEntity;
		}

		return camera.getFocusedEntity();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", ordinal = 0))
	private Iterator<Entity> appendCameraPlayerForRender(Iterable<Entity> iterable) {
		if (Playback.isReplaying && Playback.mode == ReplayView.THIRD_PERSON && Playback.manager.cameraPlayer != null) {
			if (extraEntities.size() < 1) {
				extraEntities.add(Playback.manager.cameraPlayer.getPlayer());
			}

			extraEntities.set(0, Playback.manager.cameraPlayer.getPlayer());
			return Iterables.concat(iterable, extraEntities).iterator();
		} else {
			return iterable.iterator();
		}
	}

}
