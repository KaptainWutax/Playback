package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.LoadingScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecordingSummary {
    public final File file;
    public final long length;
    public final long duration;

    RecordingSummary(File file, long length, long duration) {
        this.file = file;
        this.length = length;
        this.duration = duration;
    }

    public static RecordingSummary read(File file) throws IOException {
        try (Recording r = new Recording(file, "r")) {
            return r.readSummary();
        }
    }

    public void load() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            LoadingScreen loadingScreen = new LoadingScreen(new LiteralText("Loading Recording"));
            client.openScreen(loadingScreen);
            if (file == null) {
                loadingScreen.accept(1);
                load(Playback.recording);
            } else {
                Recording r = new Recording(file, "r");
                r.loadAsync(loadingScreen).thenRun(() -> load(r));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void load(Recording recording) {
        MinecraftClient.getInstance().send(() -> {
            Playback.recording = recording;
            Playback.restart();
            Playback.isReplaying = true;
            MinecraftClient.getInstance().startIntegratedServer("replay", "Replay", new LevelInfo(0, GameMode.SPECTATOR, false, false, LevelGeneratorType.DEFAULT));
        });
    }
}
