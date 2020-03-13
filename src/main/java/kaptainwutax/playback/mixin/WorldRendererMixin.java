package kaptainwutax.playback.mixin;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.ReplayView;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Redirect(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
	private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera) {
		if(Playback.isReplaying && Playback.manager.getView() == ReplayView.THIRD_PERSON && Playback.manager.replayPlayer != null)  {
			return Playback.manager.replayPlayer.getPlayer();
		}

		return camera.getFocusedEntity();
	}

}
