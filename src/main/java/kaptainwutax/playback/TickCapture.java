package kaptainwutax.playback;

import kaptainwutax.playback.action.IAction;
import kaptainwutax.playback.action.KeyAction;
import kaptainwutax.playback.action.MouseAction;
import kaptainwutax.playback.action.PacketAction;
import net.minecraft.network.Packet;

import java.util.*;

public class TickCapture {

	public static final TickCapture EMPTY = new TickCapture();

	protected List<IAction> actions = new ArrayList<>();
	protected Map<Long, Set<Integer>> keyStates = new HashMap<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(IAction::play);
	}

	public void addPacketAction(Packet<?> packet) {
		this.actions.add(new PacketAction(packet));
	}

	public void addKeyAction(int action, long window, int key, int scanCode, int i, int j) {
		this.actions.add(new KeyAction(action, window, key, scanCode, i, j));
	}

	public void addMouseAction(int action, long window, double d1, double d2, int i1) {
		this.actions.add(new MouseAction(action, window, d1, d2, i1));
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
