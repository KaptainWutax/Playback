package kaptainwutax.playback.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class PlaybackBrowserScreen
        extends Screen {
    protected final Screen parent;
    protected TextFieldWidget searchBox;
    private ButtonWidget deleteButton;
    private ButtonWidget loadButton;
    private ButtonWidget editButton;
    private PlaybackListWidget recordingList;

    public PlaybackBrowserScreen(Screen parent) {
        super(Text.translatable("selectPlayback.title"));
        this.parent = parent;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("selectPlayback.search"));
        this.searchBox.setChangedListener(search -> this.recordingList.setSearch((String) search));
        this.recordingList = new PlaybackListWidget(this, this.client, this.width, this.height, 48 /*TODO MAYBE 32 here*/, this.height - 64, 36, this.searchBox.getText(), this.recordingList);
        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.recordingList);
        this.loadButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectPlayback.load"), button -> this.recordingList.getSelectedAsOptional().ifPresent(PlaybackListWidget.ReplayEntry::loadPlayback)).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.editButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectPlayback.edit"), button -> this.recordingList.getSelectedAsOptional().ifPresent(PlaybackListWidget.ReplayEntry::edit)).dimensions(this.width / 2 - 154, this.height - 28, 72, 20).build());
        this.deleteButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectPlayback.delete"), button -> this.recordingList.getSelectedAsOptional().ifPresent(PlaybackListWidget.ReplayEntry::deleteIfConfirmed)).dimensions(this.width / 2 - 76, this.height - 28, 72, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 82, this.height - 28, 72, 20).build());
        this.playbackSelected(false);
        this.setInitialFocus(this.searchBox);

        this.loadButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Load"), (buttonWidget) -> {
            this.recordingList.getSelectedAsOptional().ifPresent((PlaybackListWidget.ReplayEntry replayEntry) -> replayEntry.getRecordingSummary().load());
        }).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.loadButton.active = false;
        this.addDrawableChild(ButtonWidget.builder(Text.literal(I18n.translate("gui.cancel")), b -> this.close())
                .dimensions(this.width / 2 + 4, this.height - 52, 150, 20)
                .build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.recordingList.render(matrices, mouseX, mouseY, delta);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        PlaybackBrowserScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void playbackSelected(boolean active) {
        this.loadButton.active = active;
        this.deleteButton.active = active;
        this.editButton.active = active;
    }

    @Override
    public void removed() {
        if (this.recordingList != null) {
            this.recordingList.children().forEach(PlaybackListWidget.Entry::close);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

