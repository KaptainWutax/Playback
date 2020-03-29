package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.Action;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TickCapture implements PlaybackSerializable {

	private List<Action> actions = new ArrayList<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(Action::play);
	}

	protected void addAction(Action action) {
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		for (Action action : actions) Action.writeAction(buf, action);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		actions.clear();
		while (buf.readableBytes() > 0) {
			actions.add(Action.readAction(buf));
		}
	}
}
