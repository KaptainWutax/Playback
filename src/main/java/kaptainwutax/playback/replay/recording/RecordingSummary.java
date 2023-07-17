package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.LoadingScreen;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.capture.StartState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class RecordingSummary {
    public final File file;
    public final @Nullable Path iconPath;
    public final int version;
    public final long length;
    public final long duration;
    public final StartState startState;

    RecordingSummary(File file, @Nullable Path iconPath, int version, long length, long duration, StartState startState) {
        this.file = file;
        this.iconPath = iconPath;
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
            LoadingScreen loadingScreen = new LoadingScreen(Text.literal("Loading Recording"));
            client.setScreen(loadingScreen);
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
            GeneratorOptions impl = GeneratorOptions.createRandom();
            LevelInfo levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
            levelInfo = new LevelInfo(levelInfo.getLevelName(), GameMode.CREATIVE, false, levelInfo.getDifficulty(), true, levelInfo.getGameRules(), DataConfiguration.SAFE_MODE);
            MinecraftClient.getInstance().createIntegratedServerLoader().createAndStart("Replay", levelInfo, impl, WorldPresets::createDemoOptions); //TODO why do we need an integrated server and WHY DOES IT GENERATE CHUNKS (See pause in debugger at 100% recording loading bar)
        });
    }

    public String getDisplayName() {
        return this.file.getName();
    }

    public String getName() {
        return this.file.getName();
    }

    public @Nullable Path getIconPath() {
        return this.iconPath;
    }

    public long getLastPlayed() {
        return 0;
    }

    public GameMode getGameMode() {
        if (this.startState == null) {
            return GameMode.DEFAULT;
        }
        GameJoinS2CPacket joinPacket = this.startState.getJoinPacket();
        return joinPacket == null ? GameMode.DEFAULT : joinPacket.gameMode();
    }

    public String getGameModeName() {
        if (this.startState == null) {
            return "Unknown Gamemode";
        }
        GameJoinS2CPacket joinPacket = this.startState.getJoinPacket();
        return joinPacket == null ? "Unknown Gamemode" : joinPacket.gameMode().getTranslatableName().getString();
    }

    public boolean deleteRecording() {
        return this.file.delete();
    }
}
