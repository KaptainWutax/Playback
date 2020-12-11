package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(dispatcher, model, shadowRadius);
    }

    @Override
    public boolean shouldRender(AbstractClientPlayerEntity entity, Frustum frustum, double x, double y, double z) {
        if (!Playback.getManager().renderManager.shouldRenderPlayer(entity)) return false;
        return super.shouldRender(entity, frustum, x, y, z);
    }
}
