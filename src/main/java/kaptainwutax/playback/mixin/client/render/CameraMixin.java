package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.replay.render.RenderManager;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin implements RenderManager.IAdjustCamera {
    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    public void setPosition(double x, double y, double z) {
        this.setPos(x,y,z);
    }

    public void setRotation(float roll, float pitch, float yaw) {
        if (roll != 0) {
            throw new UnsupportedOperationException();
        }
        this.setRotation(yaw, pitch);
    }
}
