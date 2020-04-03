package kaptainwutax.playback.mixin.client;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
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
			Playback.getManager().recording.getCurrentTickInfo().recordKey(0, key, scanCode, i, j);
			return;
		}
		//code duplicated to onChar Inject
		if(Playback.getManager().isReplaying()) {
			if (Playback.getManager().isProcessingReplay || Playback.getManager().cameraPlayer.options.getOptions() == MinecraftClient.getInstance().options) {
				return;
			}
			//user input, so check keybindings (e.g. toggle replay)
            Playback.getManager().cameraPlayer.options.apply(); //todo also need to swap screens etc, just not mouse grabbed state

            Playback.getManager().cameraPlayer.keyboard.onKey(window, key, scanCode, i, j);

            if(Playback.getManager().view == ReplayView.FIRST_PERSON) {
                Playback.getManager().replayPlayer.options.apply();
            }
			//user input, not allowed to reach replayPlayer
			if (Playback.getManager().currentAppliedPlayer == Playback.getManager().replayPlayer && !Playback.getManager().replayingHasFinished) {
				ci.cancel();
			}
		}
	}

	@Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
	private void onChar(long window, int i, int j, CallbackInfo ci) {
		if(MinecraftClient.getInstance().player == null) return;

		if(Playback.getManager().isRecording()) {
			Playback.getManager().recording.getCurrentTickInfo().recordKey(1, 0, 0, i, j);
		}
		//duplicated code from Inject into onKey
		if(Playback.getManager().isReplaying()) {
			if (Playback.getManager().isProcessingReplay) {
				return;
			}
			//user input, so check global keybindings (e.g. toggle replay)
//            Playback.getManager().cameraPlayer.options.apply();
//
//            Playback.getManager().cameraPlayer.keyboard.onKey(window, key, scanCode, i, j);
//
//            if(Playback.getManager().view == ReplayView.FIRST_PERSON) {
//                Playback.getManager().replayPlayer.options.apply();
//            }
            //user input, not allowed to reach replayPlayer
			if (Playback.getManager().currentAppliedPlayer == Playback.getManager().replayPlayer && !Playback.getManager().replayingHasFinished) {
				ci.cancel();
			}
		}
	}

	@Override
	public void execute(int action, int key, int scanCode, int i, int j) {
		if(action == 0) {
			this.onKey(MinecraftClient.getInstance().getWindow().getHandle(), key, scanCode, i, j);
		} else if(action == 1) {
			this.onChar(MinecraftClient.getInstance().getWindow().getHandle(), i, j);
		}
	}

}
