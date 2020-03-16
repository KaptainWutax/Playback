package kaptainwutax.playback;

import kaptainwutax.playback.capture.TickCapture;
import kaptainwutax.playback.capture.TickCaptures;

import java.util.HashMap;
import java.util.Map;

public class Recording {

	protected Map<Long, TickCaptures> recording = new HashMap<>();
	protected long endTick = Long.MAX_VALUE;


	public long currentTick = 0;
	private TickCaptures currentTickCapture = new TickCaptures();
	private TickCaptures nextTickCapture = new TickCaptures();


	public boolean isRecording(){
		return !Playback.isReplaying;
	}

	public void tickRecord(long tick) {
		if(tick == this.currentTick)return;

		if(this.currentTickCapture != null && !this.currentTickCapture.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickCapture);
		}

		this.currentTickCapture = this.nextTickCapture;
		this.nextTickCapture = new TickCaptures();
		this.currentTick = tick;
	}

	public void play(long tick) {
		this.currentTickCapture = this.recording.getOrDefault(tick, TickCaptures.EMPTY);
		this.nextTickCapture = this.recording.getOrDefault(tick+1, TickCaptures.EMPTY);
		this.currentTickCapture.play(Playback.manager.getView());
	}

	public TickCaptures getCurrentTickCapture() {
		return this.currentTickCapture;
	}

	public TickCaptures getNextTickCapture() {
		return this.nextTickCapture;
	}



	public void setEnd(){
		endTick = currentTick;
	}

	public long getEnd(){
		return endTick;
	}

}
