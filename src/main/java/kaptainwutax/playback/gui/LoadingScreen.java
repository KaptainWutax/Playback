package kaptainwutax.playback.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.DoubleConsumer;

public class LoadingScreen extends Screen implements DoubleConsumer {
    private int progress = 0;
    public boolean joining;

    public LoadingScreen(Text title) {
        super(title);
    }

    @Override
    public void accept(double progress) {
        this.progress = MathHelper.floor(progress * 100);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int center = this.width / 2;
        int barY = this.height / 2;
        int barHeight = 4;
        int border = 1;
        drawCenteredText(matrices, this.textRenderer, this.title.getString(), center, barY - 50, 0xffffff);
        drawCenteredText(matrices, this.textRenderer, progress + "%", center, barY - 20, 0xffffff);
        fill(matrices, center - 50 - border, barY - barHeight - border, center + 50 + border, barY + barHeight + border, 0xff666666);
        fill(matrices, center - 50, barY - barHeight, center - 50 + progress, barY + barHeight, 0xffffffff);
        if (joining) drawCenteredText(matrices, this.textRenderer, I18n.translate("connect.joining"), center, barY + 20, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
