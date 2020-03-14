package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;
import net.fabricmc.api.ModInitializer;

public class Playback implements ModInitializer {

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static boolean allowInputs = false;

	public static final ReplayManager manager = new ReplayManager();

	@Override
	public void onInitialize() {

	}

	public static void update() {
		if(Playback.recording.isRecording()) {
			recording.tickRecord(tickCounter);
		} else {
			if(manager.cameraPlayer == null) {
				manager.cameraPlayer = PlayerFrame.createNew();
				manager.replayPlayer = PlayerFrame.createFromExisting();
				manager.updateView(ReplayView.THIRD_PERSON);
			}

			recording.play(tickCounter);
		}

		tickCounter++;
	}

}
