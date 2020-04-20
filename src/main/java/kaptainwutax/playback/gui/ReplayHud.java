package kaptainwutax.playback.gui;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.LiteralText;

/**
 * This is the Hud that always renders. When the ReplayHudScreen is not opened (clickable), still render it.
 */
public class ReplayHud {

    //Store the state in ReplayHud and not ReplayHudScreen
    //This replay hud screen is only one of several instances, opening a ReplayHudScreen will create a new one!
    private final ReplayHudScreen replayHudScreen;

    public ReplayHud() {
        this.replayHudScreen = new ReplayHudScreen(new LiteralText("Replay HUD"));
        this.replayHudScreen.init();
    }

    public void renderReplayHud(InGameHud caller, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        //                      don't render when another replay hud screen is already visible
        if (this.isVisible() && !(MinecraftClient.getInstance().currentScreen instanceof ReplayHudScreen)) {
            Mouse mouse = Playback.getManager().cameraPlayer.mouse;
            double x = mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
            double y = mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();
            this.replayHudScreen.render((int)x,(int)y, tickDelta);
        }
    }


    public boolean isVisible() {
        return Playback.getManager().isInReplay() && !MinecraftClient.getInstance().options.hudHidden;
    }

    public interface InGameHudGetters {

    }
}
