package kaptainwutax.playback.mixin.client.options;

import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin implements PlayGameOptions.IOptionsCaller {

	@Shadow @Final @Mutable private File optionsFile;

	@Override
	public void setFile(File file) {
		this.optionsFile = file;
	}

	@Override
	public File getFile() {
		return this.optionsFile;
	}

}
