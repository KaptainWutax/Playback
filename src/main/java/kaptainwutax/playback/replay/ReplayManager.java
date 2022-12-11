package kaptainwutax.playback.replay;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.gui.WindowSize;
import kaptainwutax.playback.init.PKeyBindings;
import kaptainwutax.playback.replay.edit.ReplayEdit;
import kaptainwutax.playback.replay.recording.Recording;
import kaptainwutax.playback.replay.render.RenderManager;
import kaptainwutax.playback.replay.render.ReplayCamera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class ReplayManager {

	public Recording recording;
	public long tickCounter;

	public PlayerFrame replayPlayer;
	public PlayerFrame cameraPlayer;
	public PlayerFrame currentAppliedPlayer;

	public ReplayView view = ReplayView.THIRD_PERSON;

	public enum PlaybackState {NO_REPLAY,REPLAYING,RECORDING}
	private PlaybackState replayingState;
	public boolean isProcessingReplay;
	public boolean replayingHasFinished;
	public boolean joined;
	private boolean paused;

	public RenderManager renderManager = new RenderManager();
	public ReplayEdit replayEdit = new ReplayEdit();

	public boolean isInReplay() {
		return recording != null && this.replayingState == PlaybackState.REPLAYING;
	}

	public boolean isRecording() {
		return recording != null && this.replayingState  == PlaybackState.RECORDING;
	}

	public boolean isOrWasReplaying() {
		//todo what is this method supposed to do, why is ignoring recording == null required?
		return this.replayingState == PlaybackState.REPLAYING;
	}

	public boolean isPaused() {
		Screen screen;
		return this.paused || (this.cameraPlayer != null && (screen  = this.cameraPlayer.getCurrentScreen()) != null && screen.shouldPause());
	}

	public void setReplaying(PlaybackState state) {
		this.replayingState = state;
	}

	public void updateView(ReplayView view, boolean setCallbacks) {
		this.view = view;
		if(this.cameraPlayer == null)this.cameraPlayer = PlayerFrame.createNew();
		if(this.replayPlayer == null)this.replayPlayer = PlayerFrame.createFromExisting();

		if(view == ReplayView.FIRST_PERSON) {
			this.replayPlayer.apply(setCallbacks);
		} else if(view == ReplayView.THIRD_PERSON) {
			this.cameraPlayer.apply(setCallbacks);
		}
	}

	public void tick(boolean paused) {
		if(this.isInReplay() && this.isPaused()) return;

		if(this.isRecording()) {
			this.recording.getCurrentTickInfo().recordDebug();
			this.recording.tickRecord(++this.tickCounter);
		} else {
			this.replayEdit.tick();

			if(this.tickCounter > this.recording.getEnd()) {
				if (!this.replayingHasFinished) {
					this.replayingHasFinished = true;
					this.replayPlayer.onReplayFinished();
				}
				this.tickCounter++;
			} else {
				this.recording.playTick(this.tickCounter++);
			}
		}
	}

	public void tickFrame(boolean paused, float tickDelta) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (renderManager.isPlayingCameraPath()) {
			if (!(camera instanceof ReplayCamera)) renderManager.useReplayCamera();
		} else if (!renderManager.isRenderingActive()) {
			if (camera instanceof ReplayCamera) renderManager.useVanillaCamera();
		}
		if(!this.isInReplay() || paused || this.replayingHasFinished) return;
		this.recording.playFrame(this.tickCounter, tickDelta);
	}

	public void handleUserInput(int keyCode, int scanCode, int i) {
		if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292)) {
			PKeyBindings.updateSharedKeybindings(keyCode, scanCode, i);
		}
	}

	public boolean isOnlyAcceptingReplayedInputs() {
		return this.replayingState == PlaybackState.REPLAYING && (this.currentAppliedPlayer == null || this.currentAppliedPlayer == replayPlayer) && !this.replayingHasFinished;
	}

	public boolean isCurrentlyAcceptingInputs() {
		if (this.currentAppliedPlayer == null) {
			if (this.isInReplay()) {
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
		this.updateView(ReplayView.values()[(this.view.ordinal() + 1) % ReplayView.values().length], true);

		//Teleport the camera player to the replay player.
		this.cameraPlayer.getPlayer().updatePositionAndAngles(
				this.replayPlayer.getPlayer().getX(),
				this.replayPlayer.getPlayer().getY(),
				this.replayPlayer.getPlayer().getZ(),
				this.replayPlayer.getPlayer().getYaw(),
				this.replayPlayer.getPlayer().getPitch()
		);

		MinecraftClient.getInstance().player.sendMessage(Text.literal("Switched to " + Formatting.GREEN + this.view + Formatting.WHITE + ".").formatted(Formatting.BOLD), true);
	}

	public void togglePause() {
		this.paused = !this.paused;
	}

	public void restart(Recording recording) {
		this.replayEdit.unload();

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
		this.paused = false;
		this.replayEdit = new ReplayEdit();
	}

	public void startRecording(GameJoinS2CPacket packet) {
		if (isRecording()) {
			System.err.println("Recording already started");
			return;
		}
		try {
			recording = new Recording(Playback.getNewRecordingFile(), "rw");
			this.setReplaying(PlaybackState.RECORDING);

			//noinspection ConstantConditions
			recording.recordInitialWindowSize(((IWindowCaller)(Object)(MinecraftClient.getInstance().getWindow())).getWindowSize());

			recording.recordJoinPacket(packet);
			recording.recordPerspective(MinecraftClient.getInstance().options.getPerspective());
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
		Playback.getManager().setReplaying(ReplayManager.PlaybackState.NO_REPLAY);
	}


	public interface IWindowCaller {
		WindowSize getWindowSize();
	}
}
