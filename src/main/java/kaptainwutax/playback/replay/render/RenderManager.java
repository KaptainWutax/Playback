package kaptainwutax.playback.replay.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RenderManager {
    private static final int TPS = 20;

    private final MinecraftClient client;
    private CameraPath playingCameraPath;

    public RenderManager(MinecraftClient client) {
        this.client = client;
    }


    /**
     * Method to update the state of the camera and render manager before every rendered frame.
     * This method should be a replacement for the camera's update code when a camera path is played
     */
    public void update(long tick, float tickDelta) {
        if (this.playingCameraPath != null) {
            this.adjustCameraPositionAndRotation(this.playingCameraPath.getCameraPositionAtTime(tick,tickDelta),
                    this.playingCameraPath.getCameraRotationAtTime(tick,tickDelta));
        }
    }

    /**
     * Method to set the Minecraft Client's camera to the position and rotation
     * @param position the position of the camera
     * @param rotation the rotation of the camera, Quaternion as it allows more freedom than yaw and pitch
     */
    private void adjustCameraPositionAndRotation(Vec3d position, Quaternion rotation) {

    }

    /**
     * Used to set the framerate to a fixed value relative to the client gameticks.
     * This is useful to render videos at a fixed framerate without any lag.
     * @param frameRate the framerate we are rendering at
     */
    public static void setFramesPerTick(float frameRate) {
        float framesPerTick = frameRate / TPS;
        ((ISetForcedFrameRate)MinecraftClient.getInstance()).setFixedFrameRateForVideoRender(framesPerTick);
        ((ISetForcedFrameRate)MinecraftClient.getInstance()).setFixedFrameRateForVideoRenderEnabled(framesPerTick > 0);
    }

    public interface ISetForcedFrameRate {
        void setFixedFrameRateForVideoRender(float framesPerTick);
        void setFixedFrameRateForVideoRenderEnabled(boolean enabled);
    }


}
