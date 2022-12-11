package kaptainwutax.playback.replay.render;

import kaptainwutax.playback.util.Matrix4f;
import kaptainwutax.playback.util.Quaternion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class ReplayCamera extends Camera {
    private final CameraState.Mutable state = new CameraState.Mutable();
    private final MinecraftClient client = MinecraftClient.getInstance();

    public Matrix4f getBasicProjectionMatrix() {
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
    public Quaternionf getRotation() {
        Quaternion old = state.getRotationQuaternion();
        return new Quaternionf(old.getX(), old.getY(), old.getZ(), old.getW());
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
