package kaptainwutax.playback;

import kaptainwutax.playback.capture.DebugHelper;
import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.DebugRotationAction;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import sun.security.ssl.Debug;

public class Playback implements ModInitializer {

	public static Recording recording = new Recording();
	public static boolean isReplaying = false;
	public static long tickCounter;
	public static ReplayView mode = ReplayView.FIRST_PERSON;

	public static boolean allowInputs = false;
	public static boolean allowInputDefault = mode == ReplayView.THIRD_PERSON;

	public static final ReplayManager manager = new ReplayManager();

	@Override
	public void onInitialize() {
	}

	public static void update() {
		allowInputDefault = mode == ReplayView.THIRD_PERSON || tickCounter > recording.getEnd();
		if(Playback.recording.isRecording()) {
			//Player position debug
			PlayerEntity p = MinecraftClient.getInstance().player;
			if (p != null && p.getEntityWorld() != null) {
				DebugHelper.trackEntity(MinecraftClient.getInstance().player);
				Playback.recording.getCurrentTickCapture().recordDebugVelocity(p.getVelocity().x, p.getVelocity().y, p.getVelocity().z);
				Playback.recording.getCurrentTickCapture().recordDebugPosition(p.getX(), p.getY(), p.getZ());
				Playback.recording.getCurrentTickCapture().recordDebugRotation(p.getPitch(1F), p.getYaw(1F));
			}
			recording.tickRecord(tickCounter++);
		} else {
			if (tickCounter > recording.getEnd()) {
				allowInputs = true;
			}

			if(manager.cameraPlayer == null) {
				manager.updateView(mode);
				DebugHelper.trackEntity(Playback.manager.replayPlayer.getPlayer());
			}

			recording.play(++tickCounter);
		}
	}

	public static void restart(){ //restart the replay (intended to have to reload the world right now as well)
		Playback.tickCounter = 0;
		Playback.manager.cameraPlayer = null;
		Playback.manager.replayPlayer = null;

		DebugHelper.restartReplaying();
	}

	public static void resetRecording(){ //untested, idk when to invoke either
		recording = new Recording();
		isReplaying = false;
	}

	public static void toggleView(){ //only for between two replays for now
		if (mode == ReplayView.THIRD_PERSON)
			mode = ReplayView.FIRST_PERSON;
		else
			mode = ReplayView.THIRD_PERSON;

		allowInputDefault = mode == ReplayView.THIRD_PERSON || tickCounter > recording.getEnd();
	}
}
