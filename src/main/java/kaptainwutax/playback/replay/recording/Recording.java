package kaptainwutax.playback.replay.recording;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.entity.FakePlayer;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

public class Recording {

	protected Map<Long, TickInfo> recording = new HashMap<>();
	protected long endTick = Long.MAX_VALUE;

	transient public long currentTick = 0;
	transient private TickInfo previousTickInfo = new TickInfo();
	transient private TickInfo currentTickInfo = new TickInfo();
	transient private TickInfo nextTickInfo = new TickInfo();

	public PacketAction joinPacket;

	public boolean isRecording() {
		return !Playback.isReplaying;
	}

	public void tickRecord(long tick) {
		if(tick == this.currentTick) return;

		if(!this.currentTickInfo.isEmpty()) {
			this.recording.put(this.currentTick, this.currentTickInfo);
		}

		this.previousTickInfo = this.currentTickInfo;
		this.currentTickInfo = this.nextTickInfo;
		this.nextTickInfo = new TickInfo();

		this.currentTickInfo.third.setKeyAction(this.previousTickInfo.third.getKeyAction().copy());

		this.currentTick = tick;
	}

	public void play(long tick) {
		this.previousTickInfo = this.recording.getOrDefault(tick - 1, TickInfo.EMPTY);
		this.currentTickInfo = this.recording.getOrDefault(tick, TickInfo.EMPTY);
		this.nextTickInfo = this.recording.getOrDefault(tick + 1, TickInfo.EMPTY);
		this.currentTickInfo.play(Playback.manager.getView());
	}

	public void playUpTo(long tick) {
		for(long i = 0; i < tick; i++) {
			this.play(i);
			MinecraftClient.getInstance().tick();
		}
	}

	public TickInfo getPreviousTickInfo() {
		return this.previousTickInfo;
	}

	public TickInfo getCurrentTickInfo() {
		return this.currentTickInfo;
	}

	public TickInfo getNextTickInfo() {
		return this.nextTickInfo;
	}


	public void setEnd() {
		endTick = currentTick;
	}

	public long getEnd() {
		return endTick;
	}

}
