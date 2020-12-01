package kaptainwutax.playback.replay.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import kaptainwutax.playback.replay.encoding.Encoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import sun.nio.ch.DirectBuffer;

import static org.lwjgl.opengl.GL11.*;

public class RenderingSession {
    private final RenderManager manager;
    private final CameraPath path;
    private final Encoder<?> encoder;
    final Framebuffer framebuffer;
    private final int framesTotal;
    private int framesRendered;
    private boolean paused = true;
    private boolean inFrame;
    CameraState cameraState;

    public RenderingSession(RenderManager manager, CameraPath path, Encoder<?> encoder) {
        this.manager = manager;
        this.path = path;
        this.framesTotal = path.getFrames();
        this.encoder = encoder;
        this.framebuffer = new Framebuffer(encoder.options.width, encoder.options.height, true, MinecraftClient.IS_SYSTEM_MAC);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isActive() {
        return !this.paused && this.framesRendered < this.framesTotal;
    }

    public boolean isRenderingFrame() {
        return this.inFrame;
    }

    public void render() {
        if (!isActive()) return;
        // TODO: render out of order for paths going back in time
        int frame = this.framesRendered;
        try {
            if (frame == 0) this.encoder.open();
            this.cameraState = this.path.getCameraStateAtTime(frame);
            this.inFrame = true;
            this.encoder.captureFrame((buf, format) -> renderFrame(framebuffer, buf, format));
            this.inFrame = false;
            MinecraftClient.getInstance().onResolutionChanged();
            this.framesRendered++;
            if (this.framesRendered == this.framesTotal) encoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderFrame(Framebuffer framebuffer, DirectBuffer buf, int format) {
        MinecraftClient client = MinecraftClient.getInstance();
        Profiler profiler = client.getProfiler();
        profiler.push("playback:video_render");
        profiler.push("setup");
        client.onResolutionChanged();
        RenderSystem.pushMatrix();
        framebuffer.beginWrite(true);
        RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        profiler.swap("fog");
        BackgroundRenderer.method_23792();
        profiler.swap("render");
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        if (!client.skipGameRender) {
            profiler.push("gameRenderer");
            // Long.MAX_VALUE: give updateChunks() as much time as it needs
            // TODO: normal rendering passes a different tickDelta for paused, do we need that as well?
            client.world.setTimeOfDay(6000);
            client.chunkCullingEnabled = false;
            client.gameRenderer.render(cameraState.time.tickDelta, Long.MAX_VALUE, true);
            // client.chunkCullingEnabled = true;
            profiler.swap("toasts");
            client.getToastManager().draw(new MatrixStack());
            profiler.pop();
        }
        framebuffer.endWrite();
        RenderSystem.popMatrix();
        if (buf != null) {
            profiler.swap("read");
            framebuffer.beginRead();
            GlStateManager.getTexImage(GL_TEXTURE_2D, 0,  format, GL_UNSIGNED_BYTE, buf.address());
            framebuffer.endRead();
        }
        profiler.pop();
        profiler.pop();
    }
}
