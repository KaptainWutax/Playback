package kaptainwutax.playback.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderDispatcher.class)
public interface InvokerEntityRenderDispatcher {

    @Accessor("heldItemRenderer")
    void setHeldItemRenderer(HeldItemRenderer renderer);

}
