package kaptainwutax.playback.replay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.ReplayHud;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.encoding.LibAvEncoder;
import kaptainwutax.playback.replay.render.interpolation.CatmullRomSplineInterpolator;
import kaptainwutax.playback.replay.render.interpolation.ComponentKey;
import kaptainwutax.playback.replay.render.interpolation.HierarchyInterpolator;
import kaptainwutax.playback.replay.render.interpolation.LinearInterpolator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class RenderManager {
    private static final int TPS = 20;

    private final MinecraftClient client;
    private CameraPath exampleCameraPath;

    public final ReplayHud replayHud;

    private Collection<CameraPath> cameraPaths = new ArrayList<>();
    private CameraPath selectedCameraPath;
    private CameraPath playingCameraPath;
    private CameraState lastCameraState;

    protected Random random;
    private int cameraPathProgress;

    private Camera vanillaCamera;
    private final ReplayCamera replayCamera = new ReplayCamera();

    private RenderingSession renderingSession;

    public RenderManager() {
        this.client = MinecraftClient.getInstance();
        HierarchyInterpolator interp1 = new HierarchyInterpolator(new CatmullRomSplineInterpolator(0.5));
        HierarchyInterpolator interp2 = new HierarchyInterpolator(new CatmullRomSplineInterpolator(0.5));
        interp1.put(ComponentKey.YAW, LinearInterpolator.INSTANCE);
        this.exampleCameraPath = new KeyFrameCameraPath(451)
            .keyFrame(new KeyFrame(0, new GameTimeStamp(0, 0), 0, 70, 0, -45, 80, 0, 90, true, false))
            .interpolate(interp2)
            .keyFrame(new KeyFrame(301, new GameTimeStamp(100, 0.3f), 30, 70, 0, 360 + 45, 50, -20, 50, false, false))
            .interpolate(interp1)
            .keyFrame(new KeyFrame(451, new GameTimeStamp(150, 0.3f), 45, 90, 0, 45, 0, 20, 120, true, false));
        /*
        Dynamic<?> config = new Dynamic<>(JsonOps.INSTANCE, JsonHelper.deserialize("{\"type\":\"playback:key_frames\",\"value\":[{\"type\":\"key_frame\",\"value\":{\"position\":[0.0,70.0,0.0],\"rotation\":[-45.0,80.0,0.0],\"fov\":90.0,\"tick\":0,\"tickDelta\":0.0,\"frame\":0}},{\"type\":\"interpolation\",\"value\":{\"playback:yaw\":{\"type\":\"playback:linear\"}}},{\"type\":\"key_frame\",\"value\":{\"position\":[30.0,70.0,0.0],\"rotation\":[405.0,50.0,-20.0],\"fov\":50.0,\"tick\":100,\"tickDelta\":0.3,\"frame\":301}},{\"type\":\"key_frame\",\"value\":{\"position\":[45.0,90.0,0.0],\"rotation\":[45.0,0.0,20.0],\"fov\":120.0,\"tick\":150,\"tickDelta\":0.3,\"frame\":451}}]}"));
        this.exampleCameraPath = CameraPath.deserialize(config);
        System.out.println(CameraPath.serialize(JsonOps.INSTANCE, exampleCameraPath));
         */
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
            if (this.playingCameraPath != null) {
                this.playingCameraPath = null;
                setFrameRate(0);
                return;
            }

            setFrameRate(60);
            this.playingCameraPath = exampleCameraPath;
            this.cameraPathProgress = 0;
        } else {
            throw new IllegalStateException("Only start playing camera paths while replaying!");
        }
    }

    public void startExampleRendering() {
        if (this.renderingSession != null) {
            this.renderingSession.setPaused(true);
            this.renderingSession = null;
        } else {
            LibAvEncoder.Options options = new LibAvEncoder.Options(1280, 720, 60);
            try {
                RenderingSession session = new RenderingSession(this, exampleCameraPath, new LibAvEncoder(options));
                startRendering(session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRendering(RenderingSession session) {
        if (!Playback.getManager().isInReplay()) throw new IllegalStateException("Can only render when replaying");
        this.renderingSession = session;
        useReplayCamera();
        session.setPaused(false);
    }

    public boolean isRendering() {
        return this.renderingSession != null && this.renderingSession.isRenderingFrame();
    }

    public Framebuffer getFramebuffer() {
        return isRendering() ? this.renderingSession.framebuffer : null;
    }

    public boolean isRenderingActive() {
        return this.renderingSession != null && this.renderingSession.isActive();
    }

    @Nullable
    public CameraPath getPlayingCameraPath() {
        return this.playingCameraPath;
    }

    public double getCameraPathProgress() {
        if (!Playback.getManager().isInReplay()) return -1D;
        return this.cameraPathProgress;
    }

    @Nullable
    public GameTimeStamp getCurrentCameraPathTime() {
        if (this.playingCameraPath == null) return null;
        return this.playingCameraPath.getCameraStateAtTime(this.cameraPathProgress).time;
    }

    /**
     * Update the state of the camera and render manager before every rendered frame.
     * This method should be a replacement for the camera's update code when a camera path is played
     */
    public void updateCameraForCameraPath() {
        if (!Playback.getManager().isInReplay()) return;
        if (isRendering()) {
            if (renderingSession.cameraState != null) setCameraState(renderingSession.cameraState, true);
            return;
        }

        if (this.playingCameraPath != null) {
            int frame = this.cameraPathProgress++;
            if (this.playingCameraPath.getFrames() < frame) {
                this.playingCameraPath = null;
                setFrameRate(0);
            } else {
                this.setCameraState(this.playingCameraPath.getCameraStateAtTime(frame), false);
            }
        }
    }

    public CameraState getCurrentCameraState() {
        if (!Playback.getManager().isInReplay()) return null;
        if (isRendering()) return renderingSession.cameraState;
        if (this.playingCameraPath != null) return this.lastCameraState;
        return null;
    }

    public void checkRender() {
        if (this.renderingSession != null) {
            this.renderingSession.render();
            if (!this.renderingSession.isActive()) {
                this.renderingSession = null;
                useVanillaCamera();
            }
        }
    }

    public void render(MatrixStack matrices, float tickDelta, Camera camera) {
        if (playingCameraPath != null || isRendering()) return;
        if (selectedCameraPath != null) {
            renderCameraPath(matrices, tickDelta, camera, selectedCameraPath);
        }
    }

    public void renderCameraPath(MatrixStack matrices, float tickDelta, Camera camera, CameraPath path) {
        matrices.push();
        Vec3d camPos = camera.getPos();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        //Matrix4f matrix = matrices.peek().getModel();
        RenderSystem.enableDepthTest();
        /*
        RenderSystem.shadeModel(GL_SMOOTH);
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        float rotationOffset = 0.5f;
        glPointSize(4.0F);
        RenderSystem.lineWidth(2.0F);
        if (path instanceof KeyFrameCameraPath) {
            List<KeyFrame> keyFrames = ((KeyFrameCameraPath) path).getKeyFrames();
            if (!keyFrames.isEmpty()) {
                // keyframe positions
                buf.begin(GL_POINTS, VertexFormats.POSITION_COLOR);
                for (KeyFrame kf : keyFrames) {
                    buf.vertex(matrix, (float) kf.x, (float) kf.y, (float) kf.z)
                        .color(0, 255, 0, 255)
                        .next();
                }
                tess.draw();
                // keyframe rotation vectors
                buf.begin(GL_LINES, VertexFormats.POSITION_COLOR);
                for (KeyFrame kf : keyFrames) {
                    Vector3f forward = new Vector3f(0, 0, rotationOffset);
                    forward.rotate(kf.getRotationQuaternion());
                    buf.vertex(matrix, (float) kf.x, (float) kf.y, (float) kf.z)
                            .color(255, 160, 128, 255)
                            .next();
                    buf.vertex(matrix,
                            (float) kf.x + forward.getX(),
                            (float) kf.y + forward.getY(),
                            (float) kf.z + forward.getZ())
                            .color(255, 160, 128, 255)
                            .next();
                }
                tess.draw();
            }
        }
        int frames = path.getFrames();
        if (frames > 0) {
            // path
            buf.begin(GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
            for (int frame = 0; frame < frames; frame++) {
                CameraState state = path.getCameraStateAtTime(frame);
                if (state == null) continue;
                buf.vertex(matrix, (float) state.getX(), (float) state.getY(), (float) state.getZ())
                    .color(0, 128, 255, 255)
                    .next();
            }
            tess.draw();
            // path offset by rotation vector
            buf.begin(GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
            for (int frame = 0; frame < frames; frame++) {
                CameraState state = path.getCameraStateAtTime(frame);
                if (state == null) continue;
                Vector3f forward = new Vector3f(0, 0, rotationOffset);
                forward.rotate(state.getRotationQuaternion());
                buf.vertex(matrix,
                        (float) state.getX() + forward.getX(),
                        (float) state.getY() + forward.getY(),
                        (float) state.getZ() + forward.getZ())
                        .color(255, 128, 128, 128)
                        .next();
            }
            tess.draw();
        }
        RenderSystem.enableCull();
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(GL_FLAT);
        matrices.pop();*/
    }

    /**
     * Method to set the Minecraft Client's camera to the specified state
     * @param state the state of the camera
     * @param seek whether to seek the recording to the state's timestamp
     */
    private void setCameraState(CameraState state, boolean seek) {
        // TODO: implement seek
        replayCamera.setState(state);
        lastCameraState = state;
    }

    public void useVanillaCamera() {
        if (vanillaCamera == null) throw new IllegalStateException();
        ((MutableCamera) client.gameRenderer).setCamera(vanillaCamera);
    }

    public void useReplayCamera() {
        if (vanillaCamera == null) vanillaCamera = client.gameRenderer.getCamera();
        ((MutableCamera) client.gameRenderer).setCamera(replayCamera);
    }

    public boolean isPlayingCameraPath() {
        return playingCameraPath != null;
    }

    public CameraPath getSelectedCameraPath() {
        return selectedCameraPath;
    }

    public boolean shouldRenderPlayer(AbstractClientPlayerEntity entity) {
        ReplayManager manager = Playback.getManager();
        if (!manager.isInReplay()) return true;
        if (entity == manager.cameraPlayer.getPlayer()) return false;
        if (entity == manager.replayPlayer.getPlayer()) {
            if (manager.view == ReplayView.FIRST_PERSON) {
                return !this.client.options.getPerspective().isFirstPerson();
            }
            CameraState state = getCurrentCameraState();
            return state == null || state.isRenderPlayer();
        }
        return true;
    }

    public interface MutableCamera {
        void setCamera(Camera camera);
    }

    /**
     * Used to set the framerate to a fixed value relative to the client gameticks.
     * This is useful to render videos at a fixed framerate without any lag.
     * @param frameRate the framerate we are rendering at
     */
    public static void setFrameRate(float frameRate) {
        float framesPerTick = frameRate / TPS;
        ((ISetForcedFrameRate)MinecraftClient.getInstance()).setFixedFrameRateForVideoRender(framesPerTick);
        ((ISetForcedFrameRate)MinecraftClient.getInstance()).setFixedFrameRateForVideoRenderEnabled(framesPerTick > 0);
    }

    public interface ISetForcedFrameRate {
        void setFixedFrameRateForVideoRender(float framesPerTick);
        void setFixedFrameRateForVideoRenderEnabled(boolean enabled);
    }


}
