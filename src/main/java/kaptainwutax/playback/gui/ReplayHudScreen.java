package kaptainwutax.playback.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * ReplayHudScreen contains the clickable hud that the user can use to control keyframes, camerapaths and other
 * playback features. It is a Screen, because it needs to be clickable.
 *
 */
public class ReplayHudScreen extends Screen {

    protected List<Drawable> drawables = new ArrayList<>();
    protected GameTimeline gameTimeline;

    public ReplayHudScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.drawables.clear();
        this.drawables.add(this.gameTimeline = new GameTimeline((int)(this.width * 0.0125f), (int)(this.height * 0.025f)));
        this.addSelectableChild(this.gameTimeline);
//        this.addButton(new ButtonWidget(10, 10, this.width / 5, 20, "ButtonTest", b -> {
//            System.out.println("Button Test!");
//        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //this.drawables.forEach(e -> e.render(matrices, mouseX, mouseY, delta));
        //super.render(matrices, mouseX, mouseY, delta); TODO: re-add

        //Parameters have the wrong name in fillGradient: order is LEFT,TOP,RIGHT,BOTTOM
        //top left corner
        //this.fillGradient(0, 0, 20, 20, 0x8000ffff, 0x80ff00ff);
        //bottom right corner
        //this.fillGradient(this.width-20, this.height-20, this.width, this.height, 0x800000ff, 0x80ff00ff);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

}
