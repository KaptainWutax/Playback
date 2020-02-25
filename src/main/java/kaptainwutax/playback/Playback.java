package kaptainwutax.playback;

import net.fabricmc.api.ModInitializer;

public class Playback implements ModInitializer {

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static boolean allowInputs = false;

	@Override
	public void onInitialize() {
	}

	public static void update() {
		if(!isReplaying) {
			recording.update(tickCounter);
		} else {
			recording.play(tickCounter);
		}

		tickCounter++;
	}

}
