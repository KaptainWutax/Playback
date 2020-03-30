package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.*;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TickCapture implements PlaybackSerializable {

	private List<Action> actions = new ArrayList<>();
	protected Set<Integer> keyStates = new HashSet<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(Action::play);
	}

	protected void addAction(Action action) {
		this.actions.add(action);
	}

	public void addPacketAction(Packet<ClientPlayPacketListener> packet) {
		this.addAction(new PacketAction(packet));
	}

	public void addDebugAction(DebugAction action) {
		this.addAction(action);
	}

	public void addKeyAction(int action, int key, int scanCode, int i, int j) {
		this.addAction(new KeyAction(action, key, scanCode, i, j));
	}

	public void addMouseAction(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.addAction(new MouseAction(action, d1, d2, i1, isCursorLocked));
	}

	public void addKeyState(int i) {
		this.keyStates.add(i);
	}

	public boolean getKeyState(int i) {
		return this.keyStates.contains(i);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty() && this.keyStates.isEmpty();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.keyStates.size());
		this.keyStates.forEach(buf::writeInt);
		for (Action action : actions)Action.writeAction(buf, action);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int keyStatesSize = buf.readInt();

		for(int i = 0; i < keyStatesSize; i++) {
			this.keyStates.add(buf.readInt());
		}

		actions.clear();
		while (buf.readableBytes() > 0) {
			actions.add(Action.readAction(buf));
		}
	}
}
