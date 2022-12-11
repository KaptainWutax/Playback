package kaptainwutax.playback.gui;

import kaptainwutax.playback.replay.recording.RecordingSummary;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlaybackBrowserScreen extends Screen {
    protected final Screen parent;
    protected PlaybackListWidget list;
    ButtonWidget loadButton;

    public PlaybackBrowserScreen(Screen parent) {
        super(Text.translatable("Playback Viewer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new PlaybackListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36, this.list);
        this.addSelectableChild(this.list);
        this.loadButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Load"), (buttonWidget) -> {
            this.list.getSelectedRecording().ifPresent(RecordingSummary::load);
        }).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.loadButton.active = false;
        this.addDrawableChild(ButtonWidget.builder(Text.literal(I18n.translate("gui.cancel")), b -> this.close())
            .dimensions(this.width / 2 + 4, this.height - 52, 150, 20)
            .build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title.getString(), this.width / 2, 8, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
