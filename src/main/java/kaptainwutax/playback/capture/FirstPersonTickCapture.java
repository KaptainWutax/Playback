package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.KeyAction;
import kaptainwutax.playback.capture.action.MouseAction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FirstPersonTickCapture extends CommonTickCapture {

	protected Map<Long, Set<Integer>> keyStates = new HashMap<>();

	public FirstPersonTickCapture() {

	}

	public void addKeyAction(int action, long window, int key, int scanCode, int i, int j) {
		this.addAction(new KeyAction(action, window, key, scanCode, i, j));
	}

	public void addMouseAction(int action, long window, double d1, double d2, int i1) {
		this.addAction(new MouseAction(action, window, d1, d2, i1));
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
		return super.isEmpty() && this.keyStates.isEmpty();
	}

}
