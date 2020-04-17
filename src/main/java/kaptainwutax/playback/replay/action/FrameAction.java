package kaptainwutax.playback.replay.action;

import net.minecraft.util.PacketByteBuf;

public abstract class FrameAction extends Action {

	protected float tickDelta;

	public FrameAction() {
		this.tickDelta = client.getTickDelta();
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.tickDelta = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.tickDelta);
	}

	public float getTickDelta() {
		return this.tickDelta;
	}

}
