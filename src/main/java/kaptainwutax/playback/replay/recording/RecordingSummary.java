package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.LoadingScreen;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.capture.StartState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecordingSummary {
    public final File file;
    public final int version;
    public final long length;
    public final long duration;
    public final StartState startState;

    RecordingSummary(File file, int version, long length, long duration, StartState startState) {
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
                r.loadAsync(loadingScreen).thenRun(() -> {
                    loadingScreen.joining = true;
                    load(r);
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void load(Recording recording) {
        MinecraftClient.getInstance().send(() -> {
            Playback.getManager().restart(recording);
            Playback.getManager().setReplaying(ReplayManager.PlaybackState.REPLAYING);

            //Start an integrated server like new worlds (demo world here) are started, not like loading a world from a save.
            DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
            MinecraftClient.getInstance().method_29607("Replay", MinecraftServer.DEMO_LEVEL_INFO, impl, GeneratorOptions.method_31112(impl));
            MinecraftClient.getInstance().startIntegratedServer("Replay");
            MinecraftClient.getInstance().getServer().getPlayerManager().setCheatsAllowed(true);
        });
    }
}
