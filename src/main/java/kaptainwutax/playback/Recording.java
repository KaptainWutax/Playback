package kaptainwutax.playback;

import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.capture.TickCaptures;

import java.util.HashMap;
import java.util.Map;

public class Recording {

	protected Map<Long, TickCaptures> recording = new HashMap<>();

	protected long currentTick = 0;
	private TickCaptures currentTickCapture = new TickCaptures();

	public void tickRecord(long tick) {
		if(tick == this.currentTick)return;

		if(this.currentTickCapture != null && !this.currentTickCapture.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickCapture);
		}

		this.currentTickCapture = new TickCaptures();
		this.currentTick = tick;
	}

	public void play(long tick) {
		this.currentTickCapture = this.recording.getOrDefault(tick, TickCaptures.EMPTY);
		this.currentTickCapture.play(Playback.manager.getView());
	}

	public TickCaptures getCurrentTickCapture() {
		return this.currentTickCapture;
	}

}
