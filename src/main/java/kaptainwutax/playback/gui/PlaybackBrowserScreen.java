package kaptainwutax.playback.gui;

import kaptainwutax.playback.replay.recording.RecordingSummary;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class PlaybackBrowserScreen extends Screen {
    protected final Screen parent;
    protected PlaybackListWidget list;
    ButtonWidget loadButton;

    public PlaybackBrowserScreen(Screen parent) {
        super(new TranslatableText("Playback Viewer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new PlaybackListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36, this.list);
        this.children.add(this.list);
        this.loadButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, new LiteralText("Load"), (buttonWidget) -> {
            this.list.getSelectedRecording().ifPresent(RecordingSummary::load);
        }));
        this.loadButton.active = false;
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, new LiteralText(I18n.translate("gui.cancel")), b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.textRenderer, this.title.asString(), this.width / 2, 8, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
