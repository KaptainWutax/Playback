package kaptainwutax.playback.replay.action.third;

import kaptainwutax.playback.replay.action.Action;
import net.minecraft.util.PacketByteBuf;

public class SetFlySpeedAction extends Action {

	private float flySpeed;

	public SetFlySpeedAction() {}

	public SetFlySpeedAction(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	@Override
	public void play() {
		client.player.abilities.setFlySpeed(this.flySpeed);
	}

	@Override
	public Type getType() {
		return Type.SET_FLY_SPEED;
	}

	@Override
	public void read(PacketByteBuf buf) {
		flySpeed = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(flySpeed);
	}

}
