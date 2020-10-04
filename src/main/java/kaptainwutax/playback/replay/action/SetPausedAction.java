package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

public class SetPausedAction extends Action {

	private boolean nowPaused;

	public SetPausedAction() {
		super(true);
	}

	public SetPausedAction(boolean nowPaused) {
		this();
		this.nowPaused = nowPaused;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		super.read(buf);
		this.nowPaused = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		super.write(buf);
		buf.writeBoolean(this.nowPaused);
	}

	@Override
	public void play() {
		Playback.getManager().recording.setPaused(nowPaused);
		((ClientSetPause)MinecraftClient.getInstance()).setPaused(nowPaused);
	}

	public interface ClientSetPause {
		void setPaused(boolean paused);
	}
}