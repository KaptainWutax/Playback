package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.network.Packet;

public class CommonTickCapture extends TickCapture {

	public CommonTickCapture() {

	}

	public void addPacketAction(Packet<?> packet) {
		this.addAction(new PacketAction(packet));
	}

	public void addDebugAction(DebugAction action) {
		this.addAction(action);
	}

}