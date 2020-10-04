package kaptainwutax.playback.replay.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class ReplayCamera extends Camera {
    private final CameraState.Mutable state = new CameraState.Mutable();
    private final MinecraftClient client = MinecraftClient.getInstance();

    public Matrix4f getBasicProjectionMatrix(float tickDelta, boolean world) {
        double fov = state.getFov();
        float aspectRatio = (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight();
        return Matrix4f.viewboxMatrix(fov, aspectRatio, 0.05F, this.client.gameRenderer.getViewDistance() * 4.0F);
    }

    @Override
    public float getPitch() {
        return (float) state.getPitch();
    }

    @Override
    public float getYaw() {
        return (float) state.getYaw();
    }

    public float getRoll() {
        return (float) state.getRoll();
    }

    @Override
    public Vec3d getPos() {
        return state.getPosition();
    }

    @Override
    public Quaternion getRotation() {
        return state.getRotationQuaternion();
    }

    public void setState(CameraState state) {
        this.state.set(state);
        setPos(state.getPosition());
        Entity focused = getFocusedEntity();
        // prevent jittering, we did the interpolation for the current tickDelta already
        if (focused != null) {
            focused.lastRenderX = state.x;
            focused.lastRenderY = state.y;
            focused.lastRenderZ = state.z;
        }
        setRotation((float) state.getYaw(), (float) state.getPitch());
    }
}
