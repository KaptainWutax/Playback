package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.*;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;
import java.util.*;

public class TickCapture implements PlaybackSerializable {

	private List<Action> actions = new ArrayList<>();
	private int frameProgress = 0;

	public TickCapture() {

	}

	public void playFrame(float tickDelta) {
		for(; this.frameProgress < this.actions.size(); this.frameProgress++) {
			Action frameAction = this.actions.get(this.frameProgress);
			if(frameAction.getTickDelta() > tickDelta)break;
			frameAction.play();
		}
	}

	public void finishTick() {
		this.playFrame(1.0F);
		this.frameProgress = 0;
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

	public void addMouseAction(int action, double d1, double d2, int i1) {
		this.addAction(new MouseAction(action, d1, d2, i1));
	}

    public void addWindowFocusAction(boolean focused) {
        this.addAction(new WindowFocusAction(focused));
    }

    public void addClipboardReadAction(String clipboard) {
		//Insert the clipboard action one action earlier so it is applied just before it is used. This is necessary
		//as otherwise this action would need to be played while the one that uses the clipboard is played
		int index = this.actions.size() >= 2 ? this.actions.size() - 2 : 0;
		this.actions.add(index, new ClipboardReadAction(clipboard));
	}

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		for(Action action : this.actions) {
			Action.writeAction(buf, action);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.actions.clear();

		while(buf.readableBytes() > 0) {
			this.addAction(Action.readAction(buf));
		}
	}

}
