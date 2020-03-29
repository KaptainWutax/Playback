package kaptainwutax.playback.replay.action.third;

import kaptainwutax.playback.replay.action.Action;
import net.minecraft.util.PacketByteBuf;

public class ChangeLookAction extends Action {

	private double cursorDeltaX;
	private double cursorDeltaY;

	public ChangeLookAction() {}

	public ChangeLookAction(double cursorDeltaX, double cursorDeltaY) {
		this.cursorDeltaX = cursorDeltaX;
		this.cursorDeltaY = cursorDeltaY;
	}

	@Override
	public void play() {
		client.player.changeLookDirection(this.cursorDeltaX, this.cursorDeltaY);
	}

	@Override
	public Type getType() {
		return Type.CHANGE_LOOK;
	}

	@Override
	public void read(PacketByteBuf buf) {
		cursorDeltaX = buf.readDouble();
		cursorDeltaY = buf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(cursorDeltaX);
		buf.writeDouble(cursorDeltaY);
	}

}
