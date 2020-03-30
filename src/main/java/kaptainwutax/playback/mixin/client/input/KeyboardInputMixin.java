package kaptainwutax.playback.mixin.client.input;

import kaptainwutax.playback.replay.PlayerFrame;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin implements PlayerFrame.IKeyboardInputCaller {

	@Mutable @Shadow @Final private GameOptions settings;

	@Override
	public void setOptions(GameOptions options) {
		this.settings = options;
	}

}
