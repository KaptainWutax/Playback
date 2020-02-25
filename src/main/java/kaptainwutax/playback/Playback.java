package kaptainwutax.playback;

import net.fabricmc.api.ModInitializer;

public class Playback implements ModInitializer {

	public static Recording recording = new Recording();
	public static boolean play = false;
	public static long tick;
	public static boolean keysOpen = false;

	@Override
	public void onInitialize() {
	}

	public static void update() {
		if(!play) {
			recording.update(tick);
		} else {
			recording.play(tick);
		}

		tick++;
	}

}
