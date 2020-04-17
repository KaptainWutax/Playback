package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.PacketByteBuf;

public class GameOptionsAction extends Action {

	private String contents;

	public GameOptionsAction() {

	}

	public GameOptionsAction(GameOptions options) {
		this.contents = PlayGameOptions.getContents(options);
	}

	@Override
	public void play() {
		PlayGameOptions.loadContents(MinecraftClient.getInstance().options, this.contents);
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.contents = buf.readString();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.contents);
	}

}
