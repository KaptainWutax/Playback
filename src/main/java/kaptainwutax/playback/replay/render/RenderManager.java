package kaptainwutax.playback.replay.render;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public class RenderManager {
    private static final int TPS = 20;

    private final MinecraftClient client;
    private CameraPath exampleCameraPath = new KeyFrameCameraPath().add(
            new KeyFrame(0, 70, 0, 0, 80, -45, 0, 0)).add(
            new KeyFrame(30, 70, 0, 0, 50, 45, 100, 0.3f));


    private CameraPath playingCameraPath = exampleCameraPath;

    public RenderManager() {
        this.client = MinecraftClient.getInstance();
    }


    /**
     * Method to update the state of the camera and render manager before every rendered frame.
     * This method should be a replacement for the camera's update code when a camera path is played
     */
    public void updateCameraForCameraPath(long tick, float tickDelta) {
        if (!Playback.getManager().isReplaying()) return;

        if (this.playingCameraPath != null && this.playingCameraPath.getStartTime().compareTo(tick, tickDelta) <= 0) {
            if (this.playingCameraPath.getEndTime().compareTo(tick, tickDelta) < 0) {
                //this.playingCameraPath = null; //currently reusing the same path all the time
            } else {
                this.adjustCameraPositionAndRotation(this.playingCameraPath.getCameraPositionAtTime(tick,tickDelta),
                        this.playingCameraPath.getCameraRotationAtTime(tick,tickDelta));
                return;
            }
        }
    }

    /**
     * Method to set the Minecraft Client's camera to the position and rotation
     * @param position the position of the camera
     * @param rollPitchYaw the rotation of the camera
     */
    private void adjustCameraPositionAndRotation(Vec3d position, Vector3f rollPitchYaw) {
        IAdjustCamera camera = ((IAdjustCamera)client.gameRenderer.getCamera());
        camera.setPosition(position.x, position.y, position.z);
        camera.setRotation(rollPitchYaw.getX(), rollPitchYaw.getY(), rollPitchYaw.getZ());
    }

    public interface IAdjustCamera {
        void setPosition(double x, double y, double z);
        void setRotation(float roll, float pitch, float yaw);
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
