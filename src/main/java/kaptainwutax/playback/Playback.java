package kaptainwutax.playback;

import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Playback implements ModInitializer {

	public static final String MOD_ID = "playback";
	public static final String FILE_EXTENSION = ".pbk";
	private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private static final ReplayManager manager = new ReplayManager();

	@Override
	public void onInitialize() {
		KeyBindings.registerKeyCategories();
		KeyBindings.registerKeyBindings();
	}

	public static ReplayManager getManager() {
		return manager;
	}

	public static Identifier createIdentifier(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static File getRecordingsFolder() throws IOException {
		File recordingsFolder = new File("playback");
		if (!recordingsFolder.exists()) {
			if (!recordingsFolder.mkdirs()) throw new IOException("Could not create output directory");
		} else if (!recordingsFolder.isDirectory()) {
			throw new IOException("playback/ is not a directory");
		}
		return recordingsFolder;
	}

	public static File getNewRecordingFile() throws IOException {
		return new File(getRecordingsFolder(), FILE_NAME_FORMAT.format(new Date()) + FILE_EXTENSION);
	}

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}
}
