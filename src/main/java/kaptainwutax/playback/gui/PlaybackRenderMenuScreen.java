package kaptainwutax.playback.gui;

import kaptainwutax.playback.Playback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class PlaybackRenderMenuScreen extends Screen {
    ButtonWidget startRenderButton;

    public PlaybackRenderMenuScreen() {
        super(new TranslatableText("Playback Render Options"));
    }

    @Override
    protected void init() {
        this.startRenderButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, new LiteralText("Render video"), (buttonWidget) -> {
            this.onClose();
            Playback.getManager().renderManager.startExampleRendering();
        }));
        this.startRenderButton.active = true;
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, new LiteralText(I18n.translate("gui.cancel")), b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredString(matrices, this.textRenderer, this.title.asString(), this.width / 2, 8, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.client.openScreen(null);
    }
}
