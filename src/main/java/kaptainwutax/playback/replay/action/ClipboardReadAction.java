package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class ClipboardReadAction extends Action {

	private String clipboard;

	public ClipboardReadAction() {

	}

	public ClipboardReadAction(String clipboard) {
		this.clipboard = clipboard;
	}

	@Override
	public void play() {
		Playback.getManager().recording.setClipboard(this.clipboard);
	}

	@Override
	public Type getType() {
		return Type.CLIPBOARD_READ;
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
