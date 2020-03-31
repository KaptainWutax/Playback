package kaptainwutax.playback.replay;

import kaptainwutax.playback.replay.recording.Recording;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ReplayManager {

	public Recording recording = new Recording();
	public long tickCounter;

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	public PlayerFrame currentAppliedPlayer;

	public ReplayView view = ReplayView.FIRST_PERSON;

	private boolean isReplaying;
	public boolean isProcessingReplay;
	public boolean replayingHasFinished;
	public boolean joined;

	public boolean isReplaying() {
		return this.isReplaying;
	}

	public boolean isRecording() {
		return !this.isReplaying();
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
		if(paused)return; //todo what happens on multiplayer when the menu is opened, would the replay pause?

		if(!this.isReplaying) {
			this.recording.getCurrentTickInfo().recordDebug();
			recording.tickRecord(++tickCounter);
		} else {
			if(tickCounter > recording.getEnd()) {
				if (!replayingHasFinished) {
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
			if (this.isReplaying) {
				System.out.println("Input permission request with no player frame! Allowing ...");
			}
			return false;
		}
		return this.isReplaying && this.currentAppliedPlayer == replayPlayer && !this.replayingHasFinished;
	}

	public boolean isCurrentlyAcceptingInputs() {
		if (this.currentAppliedPlayer == null) {
			if (this.isReplaying) {
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

		//TODO: Maybe swapping this is better.
		MinecraftClient.getInstance().getToastManager().clear();

		//Teleport the camera player to the replay player.
		this.cameraPlayer.getPlayer().updatePositionAndAngles(
				this.replayPlayer.getPlayer().getX(),
				this.replayPlayer.getPlayer().getY(),
				this.replayPlayer.getPlayer().getZ(),
				this.replayPlayer.getPlayer().yaw,
				this.replayPlayer.getPlayer().pitch
		);

		MinecraftClient.getInstance().player.sendMessage(new LiteralText("Switched to " + Formatting.GREEN + this.view + "."));
	}

	public void restart() { //restart the replay (intended to have to reload the world right now as well)
		if(cameraPlayer != null)cameraPlayer.apply();
		this.tickCounter = 0;
		this.replayingHasFinished = false;
		this.cameraPlayer = null;
		this.replayPlayer = null;
		this.joined = false;
		this.isReplaying = false;
	}

}
