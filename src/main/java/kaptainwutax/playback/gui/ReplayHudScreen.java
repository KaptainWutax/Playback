package kaptainwutax.playback.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * ReplayHudScreen contains the clickable hud that the user can use to control keyframes, camerapaths and other
 * playback features. It is a Screen, because it needs to be clickable.
 *
 * Currently the ReplayHudScreen is not clickable, because it never becomes client.currentScreen.
 */
public class ReplayHudScreen extends Screen {

    protected List<Drawable> drawables = new ArrayList<>();
    protected Timeline timeline;

    public ReplayHudScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.drawables.add(this.timeline = new Timeline(this.width / 2, 40, this.width / 2, 40));
        this.addButton(new ButtonWidget(10, 10, 100, 20, "ButtonTest", b -> {
            System.out.println("Button Test!");
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        this.drawables.forEach(e -> e.render(mouseX, mouseY, delta));
    }

    public boolean isPauseScreen() {
        return false;
    }

}
