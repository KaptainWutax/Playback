package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.DebugPositionAction;
import kaptainwutax.playback.capture.action.DebugRotationAction;
import kaptainwutax.playback.capture.action.DebugVelocityAction;
import kaptainwutax.playback.capture.action.PacketAction;
import net.minecraft.network.Packet;

public class CommonTickCapture extends TickCapture {

	public CommonTickCapture() {

	}

	public void addPacketAction(Packet<?> packet) {
		this.addAction(new PacketAction(packet));
	}

	public void addDebugPositionAction(DebugPositionAction action){
		this.addAction(action);
	}

	public void addDebugVelocityAction(DebugVelocityAction action){
		this.addAction(action);
	}

	public void addDebugRotationAction(DebugRotationAction action){
		this.addAction(action);
	}

}
