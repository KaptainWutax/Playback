package kaptainwutax.playback;

import kaptainwutax.playback.capture.TickCaptures;
import kaptainwutax.playback.capture.action.PacketAction;

import java.util.HashMap;
import java.util.Map;

public class Recording {

	protected Map<Long, TickCaptures> recording = new HashMap<>();
	protected long endTick = Long.MAX_VALUE;

	transient public long currentTick = 0;
	transient private TickCaptures previousTickCapture = new TickCaptures();
	transient private TickCaptures currentTickCapture = new TickCaptures();
	transient private TickCaptures nextTickCapture = new TickCaptures();

	public PacketAction joinPacket;

	public boolean isRecording() {
		return !Playback.isReplaying;
	}

	public void tickRecord(long tick) {
		if(tick == this.currentTick) return;

		if(!this.currentTickCapture.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickCapture);
		}

		this.previousTickCapture = this.currentTickCapture;
		this.currentTickCapture = this.nextTickCapture;
		this.nextTickCapture = new TickCaptures();

		this.currentTickCapture.third.setKeyAction(this.previousTickCapture.third.getKeyAction().copy());

		this.currentTick = tick;
	}

	public void play(long tick) {
		this.previousTickCapture = this.recording.getOrDefault(tick - 1, TickCaptures.EMPTY);
		this.currentTickCapture = this.recording.getOrDefault(tick, TickCaptures.EMPTY);
		this.nextTickCapture = this.recording.getOrDefault(tick + 1, TickCaptures.EMPTY);
		this.currentTickCapture.play(Playback.manager.getView());
	}

	public TickCaptures getPreviousTickCapture() {
		return this.previousTickCapture;
	}

	public TickCaptures getCurrentTickCapture() {
		return this.currentTickCapture;
	}

	public TickCaptures getNextTickCapture() {
		return this.nextTickCapture;
	}


	public void setEnd() {
		endTick = currentTick;
	}

	public long getEnd() {
		return endTick;
	}

}
