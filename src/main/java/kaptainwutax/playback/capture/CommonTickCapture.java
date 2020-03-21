package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.*;
import net.minecraft.network.Packet;

public class CommonTickCapture extends TickCapture {

	public CommonTickCapture() {

	}

	public void addPacketAction(Packet<?> packet) {
		this.addAction(new PacketAction(packet));
	}

	public void addDebugAction(IAction action){
		this.addAction(action);
	}

}
