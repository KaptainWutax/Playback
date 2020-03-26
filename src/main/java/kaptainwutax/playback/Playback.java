package kaptainwutax.playback;

import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.ReplayManager;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.recording.Recording;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;

public class Playback implements ModInitializer {

	public static final String MOD_ID = "playback";

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static boolean isCatchingUp = false;
	public static ReplayView mode = ReplayView.FIRST_PERSON;

	public static boolean isProcessingReplay = false;

	public static boolean allowInput = false;
	public static boolean allowInputDefault = mode == ReplayView.THIRD_PERSON;

	public static final ReplayManager manager = new ReplayManager();
	public static boolean joined;

	@Override
	public void onInitialize() {
		KeyBindings.registerKeyCategories();
		KeyBindings.registerKeyBindings();
	}

	public static void update(boolean paused) {
		if(paused)return; //todo what happens on multiplayer when the menu is opened, would the replay pause?

		allowInputDefault = mode == ReplayView.THIRD_PERSON || tickCounter > recording.getEnd();
		if(Playback.recording.isRecording()) {
			Playback.recording.getCurrentTickInfo().recordDebug();
			recording.tickRecord(++tickCounter);
		} else {
			if(tickCounter > recording.getEnd()) {
				allowInput = true;
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
		Playback.isCatchingUp = true;

		KeyBinding.unpressAll();

		if(mode == ReplayView.THIRD_PERSON) {
			mode = ReplayView.FIRST_PERSON;
		} else {
			mode = ReplayView.THIRD_PERSON;
		}

		recording.joinPacket.play();
		MinecraftClient.getInstance().openScreen(null);

		Playback.manager.replayPlayer = null;
		Playback.manager.cameraPlayer = null;

		manager.updateView(mode);
		Playback.manager.replayPlayer.apply();

		allowInput = false;
		allowInputDefault = mode == ReplayView.THIRD_PERSON;

		long currentTick = Playback.tickCounter;
		Playback.tickCounter = 0;
		Playback.recording.playUpTo(currentTick);
		Playback.isCatchingUp = false;

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

}
