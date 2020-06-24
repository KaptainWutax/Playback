package kaptainwutax.playback.mixin.client;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.action.KeyAction;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements KeyAction.IKeyboardCaller {

	@Shadow
	public abstract void onKey(long window, int key, int scanCode, int i, int j);

	@Shadow
	protected abstract void onChar(long window, int i, int j);

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	public void onKey(long window, int key, int scanCode, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(Playback.getManager().isRecording()) {
			Playback.getManager().recording.getCurrentTickInfo().recordKey(KeyAction.ActionType.KEY, key, scanCode, i, j);
			return;
		}
		//code duplicated to onChar Inject
		if(Playback.getManager().isInReplay()) {
			if (Playback.getManager().isProcessingReplay || Playback.getManager().cameraPlayer.options.getOptions() == MinecraftClient.getInstance().options) {
				return;
			}
			//user input but replayPlayer applied
			if (Playback.getManager().currentAppliedPlayer == Playback.getManager().replayPlayer) {
				//user input, so send to camera player (e.g. toggle replay)
				//moving this into the !replayingHasFinished would be desireable but this would break switching to the camera player,
				//as only the camera player has the custom hotkeys
				Playback.getManager().cameraPlayer.apply(false);
				Playback.getManager().handleUserInput(key, scanCode, i);
				Playback.getManager().replayPlayer.apply(false);

				if (!Playback.getManager().replayingHasFinished) {
					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
	private void onChar(long window, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(Playback.getManager().isRecording()) {
			Playback.getManager().recording.getCurrentTickInfo().recordKey(KeyAction.ActionType.CHAR, 0, 0, i, j);
		}
		//duplicated code from Inject into onKey
		if(Playback.getManager().isInReplay()) {
			if (Playback.getManager().isProcessingReplay) {
				return;
			}
            //user input, not allowed to reach replayPlayer
			if (Playback.getManager().currentAppliedPlayer == Playback.getManager().replayPlayer && !Playback.getManager().replayingHasFinished) {
				ci.cancel();
			}
		}
	}

	@Override
	public void execute(KeyAction.ActionType action, int key, int scanCode, int i, int j) {
		if(action == KeyAction.ActionType.KEY) {
			this.onKey(MinecraftClient.getInstance().getWindow().getHandle(), key, scanCode, i, j);
		} else if(action == KeyAction.ActionType.CHAR) {
			this.onChar(MinecraftClient.getInstance().getWindow().getHandle(), i, j);
		}
	}

}
