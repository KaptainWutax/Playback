package kaptainwutax.playback;

import net.minecraft.network.Packet;

import java.util.HashMap;
import java.util.Map;

public class Recording {

	protected Map<Long, TickCapture> recording = new HashMap<>();

	protected long currentTick = 0;
	private TickCapture currentTickCapture = new TickCapture();

	public void update(long tick) {
		if(tick == this.currentTick)return;

		if(this.currentTickCapture != null && !this.currentTickCapture.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickCapture);
		}

		this.currentTickCapture = new TickCapture();
		this.currentTick = tick;
	}

	public void play(long tick) {
		if(this.recording.containsKey(tick)) {
			this.recording.get(tick).play();
		}
	}

	public void recordPacket(Packet<?> packet) {
		this.currentTickCapture.addPacketAction(packet);
	}

	public void recordKey(int action, long window, int key, int scanCode, int i, int j) {
		this.currentTickCapture.addKeyAction(action, window, key, scanCode, i, j);
	}

	public void recordMouse(int action, long window, double d1, double d2, int i1) {
		//Ignore the first tick mouse crap that causes the game to freak out. I don't know how it works, but it does.
		if(this.currentTick == 0) {
			System.out.println();
			//return;
		}

		this.currentTickCapture.addMouseAction(action, window, d1, d2, i1);
	}

}
