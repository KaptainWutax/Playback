package kaptainwutax.playback.replay.render;

import net.minecraft.client.MinecraftClient;

public class RenderManager {
    private static final int TPS = 20;









    public void setRenderUpdateSpeed() {

    }

    /**
     * Used to set the framerate to a fixed value relative to the client gameticks.
     * This is useful to render videos at a fixed framerate.
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
