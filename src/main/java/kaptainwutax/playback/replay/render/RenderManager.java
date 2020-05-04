package kaptainwutax.playback.replay.render;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.ReplayHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RenderManager {
    private static final int TPS = 20;

    private final MinecraftClient client;
    private CameraPath exampleCameraPath;

    public final ReplayHud replayHud;

    private Collection<CameraPath> cameraPaths = new ArrayList<>();
    private CameraPath selectedCameraPath;
    private CameraPath playingCameraPath;
    //Set to the starting time of the playingCameraPath, assuming the camera path starts at 0 internally
    //Offset is supposed to be used when playing the path as a preview without caring about n, not when rendering the video
    private long playingCameraPathOffset;
    private float playingCameraPathOffsetDelta;
    protected Random random;

    public RenderManager() {
        this.client = MinecraftClient.getInstance();
        this.cameraPaths = new ArrayList<>();
        this.exampleCameraPath = new KeyFrameCameraPath().add(
                new KeyFrame(0, 70, 0, 0, 80, -45, 0, 0)).add(
                new KeyFrame(30, 70, 0, 0, 50, 45, 100, 0.3f)).add(
                new KeyFrame(45, 90, 0, 0, 0, 45, 150, 0.3f));
        this.selectedCameraPath = this.exampleCameraPath;
        this.playingCameraPath = null;

        this.cameraPaths.add(this.exampleCameraPath);
        this.replayHud = new ReplayHud();

        this.random = new Random();

    }

    public void saveCameraPaths() {

    }
    public void loadCameraPaths() {

    }

    public Collection<CameraPath> getCameraPaths() {
        return this.cameraPaths;
    }

    /**
     * Start playing a camera path from the given start tick and delta.
     * Use 0 to play the path for rendering the video, use the (current time - CameraPath.getStartTime) to preview the path.
     */
    public void startPlayingCameraPath(long startTick, float startTickDelta) {
        if (Playback.getManager().isInReplay()) {
            this.playingCameraPath = exampleCameraPath;
            this.playingCameraPathOffset = startTick;
            this.playingCameraPathOffsetDelta = startTickDelta;
        } else {
            throw new IllegalStateException("Only start playing camera paths while replaying!");
        }
    }

    /**
     * Update the state of the camera and render manager before every rendered frame.
     * This method should be a replacement for the camera's update code when a camera path is played
     */
    public void updateCameraForCameraPath(long tick, float tickDelta) {
        if (!Playback.getManager().isInReplay()) return;

        float sumTickDelta = (tickDelta - this.playingCameraPathOffsetDelta) % 1;
        if (sumTickDelta < 0) sumTickDelta += 1;
        tick -= this.playingCameraPathOffset + (long)(tickDelta - this.playingCameraPathOffsetDelta);
        tickDelta = sumTickDelta;

        if (this.playingCameraPath != null && this.playingCameraPath.getStartTime().compareTo(tick, tickDelta) <= 0) {
            if (this.playingCameraPath.getEndTime().compareTo(tick, tickDelta) < 0) {
                this.playingCameraPath = null;
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
