package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Playback implements ModInitializer {

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static ReplayView mode = ReplayView.FIRST_PERSON;

	public static boolean allowInputs = false;
	public static boolean allowInputDefault = mode == ReplayView.THIRD_PERSON;

	public static final ReplayManager manager = new ReplayManager();
	public static boolean joined;

	@Override
	public void onInitialize() {
	}

	public static void update(boolean paused) {
		if (paused) return; //todo what happens on multiplayer when the menu is opened, would the replay pause?

		allowInputDefault = mode == ReplayView.THIRD_PERSON || tickCounter > recording.getEnd();
		if(Playback.recording.isRecording()) {
			Playback.recording.getCurrentTickCapture().recordDebug();
			recording.tickRecord(++tickCounter);
		} else {
			if (tickCounter > recording.getEnd()) {
				allowInputs = true;
			}

			recording.play(tickCounter++);
		}
	}

	public static void restart() { //restart the replay (intended to have to reload the world right now as well)
		Playback.tickCounter = 0;
		Playback.manager.cameraPlayer = null;
		Playback.manager.replayPlayer = null;
		Playback.joined = false;
	}

	public static void resetRecording() { //untested, idk when to invoke either
		restart();
		recording = new Recording();
		isReplaying = false;
	}

	public static void toggleView() { //only for between two replays for now
		if (mode == ReplayView.THIRD_PERSON)
			mode = ReplayView.FIRST_PERSON;
		else
			mode = ReplayView.THIRD_PERSON;

		allowInputDefault = mode == ReplayView.THIRD_PERSON || tickCounter > recording.getEnd();
	}
}
