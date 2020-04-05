package kaptainwutax.playback.mixin.client.render;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.render.RenderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public class MinecraftClient_Render_Mixin implements RenderManager.ISetForcedFrameRate {

    @Shadow @Final private RenderTickCounter renderTickCounter;

    private int prevMaxFpsReplayPlayer = -1, prevMaxFpsCameraPlayer = -1;

    @Override
    public void setFixedFrameRateForVideoRender(float framesPerTick) {
        ((RenderManager.ISetForcedFrameRate)this.renderTickCounter).setFixedFrameRateForVideoRender(framesPerTick);
    }

    @Override
    public void setFixedFrameRateForVideoRenderEnabled(boolean enabled) {
        //pass it on to the renderTickCounter to adjust the tickdelta values
        ((RenderManager.ISetForcedFrameRate)this.renderTickCounter).setFixedFrameRateForVideoRenderEnabled(enabled);
        //also set no framerate limit to render as fast as possible
        if (enabled) {
            int limit = (int)Option.FRAMERATE_LIMIT.getMax();
            this.prevMaxFpsReplayPlayer = Playback.getManager().replayPlayer.options.getOptions().maxFps;
            this.prevMaxFpsCameraPlayer = Playback.getManager().cameraPlayer.options.getOptions().maxFps;
            Playback.getManager().replayPlayer.options.getOptions().maxFps = limit;
            Playback.getManager().cameraPlayer.options.getOptions().maxFps = limit;
        } else if (this.prevMaxFpsReplayPlayer != -1){
            Playback.getManager().replayPlayer.options.getOptions().maxFps = this.prevMaxFpsReplayPlayer;
            Playback.getManager().cameraPlayer.options.getOptions().maxFps = this.prevMaxFpsCameraPlayer;
            this.prevMaxFpsReplayPlayer = -1;
            this.prevMaxFpsCameraPlayer = -1;
        }

    }
}
