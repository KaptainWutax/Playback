package kaptainwutax.playback.gui;

import kaptainwutax.playback.Playback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlaybackRenderMenuScreen extends Screen {
    ButtonWidget startRenderButton;

    public PlaybackRenderMenuScreen() {
        super(Text.translatable("Playback Render Options"));
    }

    @Override
    protected void init() {
        this.startRenderButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Render video"), (buttonWidget) -> {
            this.close();
            Playback.getManager().renderManager.startExampleRendering();
        }).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());

        this.startRenderButton.active = true;
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal(I18n.translate("gui.cancel")), b -> this.close())
            .dimensions(this.width / 2 + 4, this.height - 52, 150, 20)
            .build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title.getString(), this.width / 2, 8, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }

}
