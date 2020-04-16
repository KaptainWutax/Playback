package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.recording.Recording;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class ReplayManager {

	public Recording recording;
	public long tickCounter;

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	public PlayerFrame currentAppliedPlayer;

	public ReplayView view = ReplayView.THIRD_PERSON;

	private boolean isReplaying;
	public boolean isProcessingReplay;
	public boolean replayingHasFinished;
	public boolean joined;
	private boolean paused = true;

	public boolean isReplaying() {
		return recording != null && this.isReplaying;
	}

	public boolean isRecording() {
		return recording != null && !this.isReplaying;
	}

	public boolean isOrWasReplaying() {
		return this.isReplaying;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void setReplaying(boolean flag) {
		this.isReplaying = flag;
	}

	public void updateView(ReplayView view) {
		this.view = view;
		if(this.cameraPlayer == null)this.cameraPlayer = PlayerFrame.createNew();
		if(this.replayPlayer == null)this.replayPlayer = PlayerFrame.createFromExisting();

		if(view == ReplayView.FIRST_PERSON) {
			this.replayPlayer.apply();
		} else if(view == ReplayView.THIRD_PERSON) {
			this.cameraPlayer.apply();
		}
	}

	public void tick(boolean paused) {
		if(this.isReplaying() && paused)return;

		if(this.isRecording()) {
			this.recording.getCurrentTickInfo().recordDebug();
			recording.tickRecord(++tickCounter);
		} else {
			if(tickCounter > recording.getEnd()) {
				if (!replayingHasFinished) {
					//commented out because it seems to cause problems with switching views and screens when replay finished
					replayingHasFinished = true;
					replayPlayer.onReplayFinished();
				}
			} else {
				recording.playTick(tickCounter++);
			}
		}
	}

	public boolean isOnlyAcceptingReplayedInputs() {
		if (this.currentAppliedPlayer == null) {
			if (this.isReplaying()) {
				System.out.println("Input permission request with no player frame! Allowing ...");
			}
			return false;
		}
		return this.isReplaying && this.currentAppliedPlayer == replayPlayer && !this.replayingHasFinished;
	}

	public boolean isCurrentlyAcceptingInputs() {
		if (this.currentAppliedPlayer == null) {
			if (this.isReplaying()) {
				System.out.println("Inputs with no player frame! Allowing them...");
			}
			return true;
		}

		return this.currentAppliedPlayer == cameraPlayer || this.isProcessingReplay || this.replayingHasFinished;
	}

	public ReplayView getView() {
		return this.view;
	}

	public PlayerFrame getPlayerFrameForView(ReplayView view) {
		if (view == ReplayView.FIRST_PERSON)
			return replayPlayer;
		else
			return cameraPlayer;
	}

	public void toggleView() {
		this.updateView(ReplayView.values()[(this.view.ordinal() + 1) % ReplayView.values().length]);

		//TODO: Maybe swapping the toast manager is better.
		MinecraftClient.getInstance().getToastManager().clear();

		//Teleport the camera player to the replay player.
		this.cameraPlayer.getPlayer().updatePositionAndAngles(
				this.replayPlayer.getPlayer().getX(),
				this.replayPlayer.getPlayer().getY(),
				this.replayPlayer.getPlayer().getZ(),
				this.replayPlayer.getPlayer().yaw,
				this.replayPlayer.getPlayer().pitch
		);

		MinecraftClient.getInstance().player.sendMessage(new LiteralText("Switched to " + Formatting.GREEN + this.view + Formatting.WHITE + "."));
	}

	public void togglePause() {
		this.paused = !this.paused;
	}

	public void restart(Recording recording) {
		if(cameraPlayer != null) {
			cameraPlayer.options.apply();
		}

		this.view = ReplayView.THIRD_PERSON;
		this.tickCounter = 0;
		this.replayingHasFinished = false;
		this.cameraPlayer = null;
		this.replayPlayer = null;
		this.currentAppliedPlayer = null;
		this.joined = false;
		this.recording = recording;
		this.paused = true;
	}

	public void startRecording(GameJoinS2CPacket packet) {
		if (isRecording()) {
			System.err.println("Recording already started");
			return;
		}
		try {
			recording = new Recording(Playback.getNewRecordingFile(), "rw");
			recording.recordJoinPacket(packet);
			recording.recordPerspective(MinecraftClient.getInstance().options.perspective);
			recording.recordPhysicalSide(MinecraftClient.getInstance().isInSingleplayer());
			recording.recordInitialWindowFocus(MinecraftClient.getInstance().isWindowFocused());
			recording.recordGameOptions(MinecraftClient.getInstance().options);
		} catch (IOException e) {
			e.printStackTrace();
			recording = null;
		}
	}

	public void stopRecording() {
		if (!isRecording()) return;
		try {
			recording.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		recording = null;
	}
}
