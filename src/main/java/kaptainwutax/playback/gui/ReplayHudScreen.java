package kaptainwutax.playback.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * ReplayHudScreen contains the clickable hud that the user can use to control keyframes, camerapaths and other
 * playback features. It is a Screen, because it needs to be clickable.
 *
 * Currently the ReplayHudScreen is not clickable, because it never becomes client.currentScreen.
 */
public class ReplayHudScreen extends Screen {

    public ReplayHudScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(10, 10 , 100, 20, "ButtonTest", b -> { System.out.println("Button Test!");}));
    }

    public boolean isPauseScreen() {
        return false;
    }

}
