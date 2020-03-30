package kaptainwutax.playback;

import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.recording.Recording;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Playback implements ModInitializer {
	public static final String MOD_ID = "playback";
	public static final String FILE_EXTENSION = ".pbk";
	private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static boolean isCatchingUp = false;
	public static ReplayView mode = ReplayView.FIRST_PERSON;

	public static boolean isProcessingReplay = false;
	public static boolean replayingHasFinished;

	public static final ReplayManager manager = new ReplayManager();
	public static boolean joined;

	@Override
	public void onInitialize() {
		KeyBindings.registerKeyCategories();
		KeyBindings.registerKeyBindings();
	}

	public static void update(boolean paused) {
		if(paused)return; //todo what happens on multiplayer when the menu is opened, would the replay pause?

		if(Playback.recording.isRecording()) {
			Playback.recording.getCurrentTickInfo().recordDebug();
			recording.tickRecord(++tickCounter);
		} else {
			if(tickCounter > recording.getEnd()) {
				replayingHasFinished = true;
			} else {
				recording.playTick(tickCounter++);
			}
		}
	}

	public static void restart() { //restart the replay (intended to have to reload the world right now as well)
		Playback.tickCounter = 0;
		Playback.replayingHasFinished = false;
		Playback.manager.cameraPlayer = null;
		Playback.manager.replayPlayer = null;
		Playback.joined = false;
		Playback.isReplaying = false;
	}

	public static void resetRecording() { //untested, idk when to invoke either
		restart();
		recording = new Recording();
		isReplaying = false;
	}

	public static void toggleView() {
//		Playback.isCatchingUp = true;

//		KeyBinding.unpressAll();

		if(mode == ReplayView.THIRD_PERSON) {
			mode = ReplayView.FIRST_PERSON;
		} else {
			mode = ReplayView.THIRD_PERSON;
		}

//		MinecraftClient.getInstance().world = null;
//		MinecraftClient.getInstance().openScreen(null);
//		recording.joinPacket.play();
//		MinecraftClient.getInstance().openScreen(null);
//
//		Playback.manager.replayPlayer = null;
//		Playback.manager.cameraPlayer = null;

		manager.updateView(mode);
//		Playback.manager.replayPlayer.apply();

//		long currentTick = Playback.tickCounter;
//		Playback.tickCounter = 0;
//		Playback.recording.playUpTo(currentTick);
//		Playback.isCatchingUp = false;

		//clear the advancement/achievement popups
		//swapping toast manager together with the playerframe might be better
		MinecraftClient.getInstance().getToastManager().clear();

		if(mode == ReplayView.THIRD_PERSON) {
			Playback.manager.cameraPlayer.getPlayer().updatePositionAndAngles(
					Playback.manager.replayPlayer.getPlayer().getX(),
					Playback.manager.replayPlayer.getPlayer().getY(),
					Playback.manager.replayPlayer.getPlayer().getZ(),
					Playback.manager.replayPlayer.getPlayer().yaw,
					Playback.manager.replayPlayer.getPlayer().pitch
			);
		}

		System.out.println("Switched to " + mode);
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
}
