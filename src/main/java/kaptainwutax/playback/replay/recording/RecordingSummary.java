package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.LoadingScreen;
import kaptainwutax.playback.replay.action.StartStateAction;
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
    public final int version;
    public final long length;
    public final long duration;
    public final StartStateAction startState;

    RecordingSummary(File file, int version, long length, long duration, StartStateAction startState) {
        this.file = file;
        this.version = version;
        this.length = length;
        this.duration = duration;
        this.startState = startState;
    }

    public static RecordingSummary read(File file) throws IOException {
        try (Recording r = new Recording(file, "r")) {
            return r.readSummary();
        }
    }

    public boolean canLoad() {
        return version == Recording.FORMAT_VERSION;
    }

    public void load() {
        if (version != Recording.FORMAT_VERSION) throw new IllegalStateException("Cannot load recording of version " + version);
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            LoadingScreen loadingScreen = new LoadingScreen(new LiteralText("Loading Recording"));
            client.openScreen(loadingScreen);
            if (file == null) {
                loadingScreen.accept(1);
                load(Playback.getManager().recording);
            } else {
                Recording r = new Recording(file, "r");
                Playback.getManager().recording = r;
                r.loadAsync(loadingScreen).thenRun(() -> load(r));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void load(Recording recording) {
        MinecraftClient.getInstance().send(() -> {
            Playback.getManager().recording = recording;
            Playback.getManager().restart();
            Playback.getManager().setReplaying(true);
            MinecraftClient.getInstance().startIntegratedServer("replay", "Replay", new LevelInfo(0, GameMode.SPECTATOR, false, false, LevelGeneratorType.DEFAULT));
        });
    }
}
