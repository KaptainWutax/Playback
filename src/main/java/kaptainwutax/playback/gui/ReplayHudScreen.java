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
        this.drawables.clear();
        this.drawables.add(this.timeline = new Timeline((int)(this.width * 0.0125f), (int)(this.height * 0.025f), (int)(this.width * 0.975f), this.height/6));
        this.addButton(new ButtonWidget(10, 10, this.width / 5, 20, "ButtonTest", b -> {
            System.out.println("Button Test!");
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.drawables.forEach(e -> e.render(mouseX, mouseY, delta));
        super.render(mouseX, mouseY, delta);
        //Parameters have the wrong name in fillGradient: order is LEFT,TOP,RIGHT,BOTTOM
        //top left corner
        this.fillGradient(0, 0, 20, 20, 0x8000ffff, 0x80ff00ff);
        //bottom right corner
        this.fillGradient(this.width-20, this.height-20, this.width, this.height, 0x800000ff, 0x80ff00ff);
    }

    public boolean isPauseScreen() {
        return false;
    }

}
