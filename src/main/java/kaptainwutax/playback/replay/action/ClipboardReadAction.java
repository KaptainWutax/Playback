package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.network.PacketByteBuf;
import java.io.IOException;

public class ClipboardReadAction extends Action {

	private String clipboard;

	public ClipboardReadAction() {
		super(false);
	}

	public ClipboardReadAction(String clipboard) {
		this();
		this.clipboard = clipboard;
	}

	@Override
	public void play() {
		Playback.getManager().recording.setClipboard(this.clipboard);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.clipboard = buf.readString();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.clipboard);
	}

}
