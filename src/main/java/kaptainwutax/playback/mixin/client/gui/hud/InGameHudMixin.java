package kaptainwutax.playback.mixin.client.gui.hud;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.ReplayHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin implements ReplayHud.InGameHudGetters {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", shift = At.Shift.BEFORE, ordinal = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/scoreboard/ScoreboardObjective;)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(I)V")
            )
    )
    private void renderReplayHud(float tickDelta, CallbackInfo ci) {
        Playback.getManager().renderManager.replayHud.renderReplayHud((InGameHud)(Object)this, tickDelta);
    }
}
