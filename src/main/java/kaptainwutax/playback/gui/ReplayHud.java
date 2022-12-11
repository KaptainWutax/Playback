package kaptainwutax.playback.gui;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * This is the Hud that always renders. When the ReplayHudScreen is not opened (clickable), still render it.
 */
public class ReplayHud {

    //Store no meaningful state in ReplayHudScreen
    private ReplayHudScreen replayHudScreen;

    public ReplayHud() {
    }

    public ReplayHudScreen getScreen() {
        if(MinecraftClient.getInstance().getNarratorManager() == null) {
            return null;
        }

        if (this.replayHudScreen == null) {
            this.replayHudScreen = new ReplayHudScreen(Text.literal("Replay HUD"));
            this.replayHudScreen.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        }
        return this.replayHudScreen;
    }

    public void renderReplayHud(MatrixStack matrices, InGameHud caller, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        //                      don't render when another replay hud screen is already visible
        if (this.isVisible() && !(MinecraftClient.getInstance().currentScreen instanceof ReplayHudScreen)) {
            Mouse mouse = Playback.getManager().cameraPlayer.mouse;
            double x = mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
            double y = mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();
            this.getScreen().render(matrices, (int)x,(int)y, tickDelta);
        }
    }


    public boolean isVisible() {
        return Playback.getManager().isInReplay() && !MinecraftClient.getInstance().options.hudHidden && !Playback.getManager().renderManager.isRendering();
    }

    public void resize() {
        ReplayHudScreen screen = this.getScreen();
        if(screen == null) return;
        screen.resize(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    public interface InGameHudGetters {

    }
}
