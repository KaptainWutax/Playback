package kaptainwutax.playback;

import kaptainwutax.playback.action.IAction;
import kaptainwutax.playback.action.KeyAction;
import kaptainwutax.playback.action.MouseAction;
import kaptainwutax.playback.action.PacketAction;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class TickCapture {

	protected List<IAction> actions = new ArrayList<>();

	public TickCapture() {

	}

	public void play() {
		for(IAction action: this.actions) {
			action.play();
		}
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

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

}
