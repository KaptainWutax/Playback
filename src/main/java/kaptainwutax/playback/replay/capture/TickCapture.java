package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.*;
import net.minecraft.network.Packet;

import java.util.*;

public class TickCapture {

	private List<Action> actions = new ArrayList<>();
	protected Map<Long, Set<Integer>> keyStates = new HashMap<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(Action::play);
	}

	protected void addAction(Action action) {
		this.actions.add(action);
	}

	public void addPacketAction(Packet<?> packet) {
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

	public void addF5ModeFixAction(int perspectiveF5mode) {
		this.addAction(new F5ModeFixAction(perspectiveF5mode));
	}

	public void addKeyState(long handle, int i) {
		if(!this.keyStates.containsKey(handle)) {
			this.keyStates.put(handle, new HashSet<>());
		}

		this.keyStates.get(handle).add(i);
	}

	public boolean getKeyState(long handle, int i) {
		if(!this.keyStates.containsKey(handle)) {
			return false;
		}

		return this.keyStates.get(handle).contains(i);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty() && this.keyStates.isEmpty();
	}

}
