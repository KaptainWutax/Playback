package kaptainwutax.playback.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.recording.RecordingSummary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PlaybackListWidget
        extends AlwaysSelectedEntryListWidget<PlaybackListWidget.Entry> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_playback.png");
    static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/replay_selection.png");
    static final Text INCOMPATIBLE_PLAYBACK = Text.translatable("selectReplay.tooltip.incompatible_playback").formatted(Formatting.RED);
    private final PlaybackBrowserScreen parent;
    private final LoadingEntry loadingEntry;
    private CompletableFuture<List<RecordingSummary>> recordingSummariesFuture;
    @Nullable
    private List<RecordingSummary> recordingSummaries;
    private String search;

    public PlaybackListWidget(PlaybackBrowserScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, String search, @Nullable PlaybackListWidget oldWidget) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.loadingEntry = new LoadingEntry(client);
        this.search = search;
        this.recordingSummariesFuture = oldWidget != null ? oldWidget.recordingSummariesFuture : this.loadReplaysOffthread();
        this.show(this.tryGet());
    }

    @Nullable
    private List<RecordingSummary> tryGet() {
        try {
            return this.recordingSummariesFuture.getNow(null);
        } catch (CancellationException | CompletionException runtimeException) {
            return null;
        }
    }

    void load() {
        this.recordingSummariesFuture = this.loadReplaysOffthread();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        List<RecordingSummary> list = this.tryGet();
        if (list != this.recordingSummaries) {
            this.show(list);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void show(@Nullable List<RecordingSummary> recordings) {
        if (recordings == null) {
            this.showLoadingScreen();
        } else {
            this.showSummaries(this.search, recordings);
        }
        this.recordingSummaries = recordings;
    }

    public void setSearch(String search) {
        if (this.recordingSummaries != null && !search.equals(this.search)) {
            this.showSummaries(search, this.recordingSummaries);
        }
        this.search = search;
    }

    public CompletableFuture<List<RecordingSummary>> loadReplaysOffthread() {
        this.clearEntries();
        return CompletableFuture.supplyAsync(this::loadReplays).exceptionally(throwable -> {
            this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Couldn't load replay list"));
            return List.of();
        });
    }

    public List<RecordingSummary> loadReplays() {
        List<RecordingSummary> summaries = new ArrayList<>();
        try {
            File recordingsFolder = Playback.getRecordingsFolder();
            File[] replayFiles = recordingsFolder.listFiles((d, f) -> f.endsWith(Playback.FILE_EXTENSION));
            if (replayFiles == null) {
                return summaries;
            }
            for (File replayFile : replayFiles) {
                try {
                    summaries.add(RecordingSummary.read(replayFile));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        summaries.sort((a, b) -> {
            if (a.file == null) return b.file == null ? 0 : -1;
            if (b.file == null) return 1;
            return -a.file.compareTo(b.file);
        });
        return summaries;
    }

    private void showSummaries(String search, List<RecordingSummary> summaries) {
        this.clearEntries();
        search = search.toLowerCase(Locale.ROOT);
        for (RecordingSummary levelSummary : summaries) {
            if (!this.shouldShow(search, levelSummary)) continue;
            this.addEntry(new ReplayEntry(this, levelSummary));
        }
        this.narrateScreenIfNarrationEnabled();
    }

    private boolean shouldShow(String search, RecordingSummary summary) {
        return summary.getDisplayName().toLowerCase(Locale.ROOT).contains(search);
    }

    private void showLoadingScreen() {
        this.clearEntries();
        this.addEntry(this.loadingEntry);
        this.narrateScreenIfNarrationEnabled();
    }

    private void narrateScreenIfNarrationEnabled() {
        this.parent.narrateScreenIfNarrationEnabled(true);
    }

    private void showUnableToLoadScreen(Text message) {
        this.client.setScreen(new FatalErrorScreen(Text.translatable("selectReplay.unable_to_load"), message));
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected boolean isFocused() {
        return this.parent.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.parent.playbackSelected(entry != null && entry.isAvailable());
    }

    @Override
    protected void moveSelection(EntryListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, Entry::isAvailable);
    }

    public Optional<ReplayEntry> getSelectedAsOptional() {
        Entry entry = this.getSelectedOrNull();
        if (entry instanceof ReplayEntry replayEntry) {
            return Optional.of(replayEntry);
        }
        return Optional.empty();
    }

    public PlaybackBrowserScreen getParent() {
        return this.parent;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.children().contains(this.loadingEntry)) {
            this.loadingEntry.appendNarrations(builder);
            return;
        }
        super.appendNarrations(builder);
    }

    @Environment(value = EnvType.CLIENT)
    public static class LoadingEntry
            extends Entry {
        private static final Text LOADING_LIST_TEXT = Text.translatable("selectReplay.loading_list");
        private final MinecraftClient client;

        public LoadingEntry(MinecraftClient client) {
            this.client = client;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_LIST_TEXT)) / 2;
            int j = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
            this.client.textRenderer.draw(matrices, LOADING_LIST_TEXT, (float) i, (float) j, 0xFFFFFF);
            String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
            int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
            int l = j + this.client.textRenderer.fontHeight;
            this.client.textRenderer.draw(matrices, string, (float) k, (float) l, 0x808080);
        }

        @Override
        public Text getNarration() {
            return LOADING_LIST_TEXT;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }
    }

    @Environment(value = EnvType.CLIENT)
    public static abstract class Entry
            extends AlwaysSelectedEntryListWidget.Entry<Entry>
            implements AutoCloseable {
        public abstract boolean isAvailable();

        @Override
        public void close() {
        }
    }

    @Environment(value = EnvType.CLIENT)
    public final class ReplayEntry
            extends Entry
            implements AutoCloseable {

        private final MinecraftClient client;
        private final PlaybackBrowserScreen screen;
        private final RecordingSummary recordingSummary;
        private final Identifier iconLocation;
        @Nullable
        private final NativeImageBackedTexture icon;
        @Nullable
        private Path iconPath;
        private long clickTime;

        public ReplayEntry(PlaybackListWidget levelList, RecordingSummary recording) {
            this.client = levelList.client;
            this.screen = levelList.getParent();
            this.recordingSummary = recording;
            String string = recording.getName();
            this.iconLocation = new Identifier("minecraft", "replays/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon");
            this.iconPath = recording.getIconPath();
            if (this.iconPath != null && !Files.isRegularFile(this.iconPath)) {
                this.iconPath = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public Text getNarration() {
            MutableText text = Text.translatable("narrator.select.replay", recordingSummary.getDisplayName(), Text.translatable("gameMode." + recordingSummary.getGameModeName()));
            return Text.translatable("narrator.select", text);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String replayName = recordingSummary.getDisplayName();
            //Maybe add the save date or the last access date like in singleplayer worlds
//            recordingSummary.getName() + " (" + DATE_FORMAT.format(new Date(recordingSummary.getLastPlayed())) + ")";
            if (StringUtils.isEmpty(replayName)) {
                replayName = I18n.translate("selectReplay.replay") + " " + (index + 1);
            }

            long time = recordingSummary.duration / 20;
            int seconds = (int) (time % 60);
            time = (time - seconds) / 60;
            int minutes = (int) (time % 60);
            int hours = (int) ((time - minutes) / 60);

            String duration = hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);

            GameJoinS2CPacket joinPacket = recordingSummary.startState.getJoinPacket();
            String gamemode = joinPacket == null ? "" : joinPacket.gameMode().getTranslatableName().getString();
            String multiplayer = I18n.translate(recordingSummary.startState.isSinglePlayer() ? "menu.singleplayer" : "menu.multiplayer");
            String fileSize = String.format("%.2fMB", recordingSummary.length / (1024.0 * 1024.0));

            String filesizeIsMultiplayer = fileSize + " " + multiplayer;
            String durationGamemode = duration + " " + gamemode;

            this.client.textRenderer.draw(matrices, replayName, (float) (x + 32 + 3), (float) (y + 1), 0xFFFFFF);
            this.client.textRenderer.draw(matrices, filesizeIsMultiplayer, (float) (x + 32 + 3), (float) (y + this.client.textRenderer.fontHeight + 3), 0x808080);
            this.client.textRenderer.draw(matrices, durationGamemode, (float) (x + 32 + 3), (float) (y + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3), 0x808080);


            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.client.options.getTouchscreen().getValue() || hovered) {
                int j;
                RenderSystem.setShaderTexture(0, WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                int i = mouseX - x;
                boolean bl = i < 32;
                j = bl ? 32 : 0;
                if (recordingSummary.canLoad()) {
                    DrawableHelper.drawTexture(matrices, x, y, 0.0f, j, 32, 32, 256, 256);
                } else {
                    DrawableHelper.drawTexture(matrices, x, y, 32.0f, j, 32, 32, 256, 256);
                    DrawableHelper.drawTexture(matrices, x, y, 96.0f, j, 32, 32, 256, 256);
                    if (bl) {
                        this.screen.setTooltip(ImmutableList.of(INCOMPATIBLE_PLAYBACK.asOrderedText()));
                    }
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            PlaybackListWidget.this.setSelected(this);
            if (!this.recordingSummary.canLoad()) {
                return true;
            }
            if (mouseX - (double) PlaybackListWidget.this.getRowLeft() <= 32.0) {
                this.loadPlayback();
                return true;
            }
            if (Util.getMeasuringTimeMs() - this.clickTime < 250L) {
                this.loadPlayback();
                return true;
            }
            this.clickTime = Util.getMeasuringTimeMs();
            return false;
        }

        public void loadPlayback() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.recordingSummary.canLoad()) {
                this.recordingSummary.load();
            }
        }

        public void deleteIfConfirmed() {
            this.client.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    this.client.setScreen(new ProgressScreen(true));
                    this.delete();
                }
                this.client.setScreen(this.screen);
            }, Text.translatable("selectReplay.deleteQuestion"), Text.translatable("selectReplay.deleteWarning", recordingSummary.getDisplayName()), Text.translatable("selectReplay.deleteButton"), ScreenTexts.CANCEL));
        }

        public void delete() {
            if (this.recordingSummary.deleteRecording()) {
                this.reloadContent();
            }
        }

        public void reloadContent() {
            PlaybackListWidget.this.load();
        }

        public void edit() {
            this.openReadingReplayScreen();
            String replayName = recordingSummary.getName();
            try {
                LevelStorage.Session session = this.client.getLevelStorage().createSession(replayName);
//                this.client.setScreen(new EditReplayScreen((boolean edited) -> { //TODO EditReplayScreen
//                    try {
//                        session.close();
//                    }
//                    catch (IOException iOException) {
//                        LOGGER.error("Failed to unlock replay {}", replayName, iOException);
//                    }
//                    if (edited) {
//                        PlaybackListWidget.this.load();
//                    }
//                    this.client.setScreen(this.screen);
//                }, session));
            } catch (IOException iOException) {
                SystemToast.add(client.getToastManager(), SystemToast.Type.WORLD_ACCESS_FAILURE, Text.translatable("selectReplay.access_failure"), Text.literal(replayName));
                LOGGER.error("Failed to access replay {}", replayName, iOException);
                this.reloadContent();
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(recordingSummary.getName())) {
                this.openReadingReplayScreen();
                this.client.createIntegratedServerLoader().start(this.screen, recordingSummary.getName());
            }
        }

        private void openReadingReplayScreen() {
            this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectReplay.data_read")));
        }

        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            bl = this.iconPath != null && Files.isRegularFile(this.iconPath);
            if (bl) {
                NativeImageBackedTexture nativeImageBackedTexture;
                {
                    try {
                        try (InputStream inputStream = Files.newInputStream(this.iconPath)) {
                            NativeImage nativeImage = NativeImage.read(inputStream);
                            Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide");
                            Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high");
                            NativeImageBackedTexture nativeImageBackedTexture2 = new NativeImageBackedTexture(nativeImage);
                            this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture2);
                            nativeImageBackedTexture = nativeImageBackedTexture2;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Invalid icon for replay {}", recordingSummary.getName(), e);
                        this.iconPath = null;
                        return null;
                    }
                }
                return nativeImageBackedTexture;
            }
            this.client.getTextureManager().destroyTexture(this.iconLocation);
            return null;
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }

        public String getRecordingDisplayName() {
            return recordingSummary.getDisplayName();
        }

        @Override
        public boolean isAvailable() {
            return this.recordingSummary.canLoad();
        }

        public RecordingSummary getRecordingSummary() {
            return recordingSummary;
        }
    }
}

