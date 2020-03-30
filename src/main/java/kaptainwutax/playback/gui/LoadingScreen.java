package kaptainwutax.playback.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.DoubleConsumer;

public class LoadingScreen extends Screen implements DoubleConsumer {
    private int progress = 0;

    public LoadingScreen(Text title) {
        super(title);
    }

    @Override
    public void accept(double progress) {
        this.progress = MathHelper.floor(progress * 100);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        int center = this.width / 2;
        int barY = this.height / 2;
        int barHeight = 4;
        int border = 1;
        drawCenteredString(this.font, this.title.asFormattedString(), center, barY - 50, 0xffffff);
        drawCenteredString(this.font, progress + "%", center, barY - 20, 0xffffff);
        fill(center - 50 - border, barY - barHeight - border, center + 50 + border, barY + barHeight + border, 0xff666666);
        fill(center - 50, barY - barHeight, center - 50 + progress, barY + barHeight, 0xffffffff);
        super.render(mouseX, mouseY, delta);
    }
}
