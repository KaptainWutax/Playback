package kaptainwutax.playback.replay.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;

public class MouseAction extends Action {

	private int action;
	private double d1;
	private double d2;
	private int i1;
	private boolean windowFocused;
	private boolean cursorLocked;

	public MouseAction() {}

	public MouseAction(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
		this.windowFocused = MinecraftClient.getInstance().isWindowFocused();
		this.cursorLocked = isCursorLocked;
	}

	@Override
	public void play() {
		((IMouse) client.mouse).execute(this.action, this.d1, this.d2, this.i1, this.windowFocused, this.cursorLocked);
	}

	@Override
	public Type getType() {
		return Type.MOUSE;
	}

	@Override
	public void read(PacketByteBuf buf) {
		action = buf.readVarInt();
		d1 = buf.readDouble();
		d2 = buf.readDouble();
		i1 = buf.readVarInt();
		byte flags = buf.readByte();
		windowFocused = (flags & 1) != 0;
		cursorLocked = (flags & 2) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(action);
		buf.writeDouble(d1);
		buf.writeDouble(d2);
		buf.writeVarInt(i1);
		byte flags = 0;
		if (windowFocused) flags |= 1;
		if (cursorLocked) flags |= 2;
		buf.writeByte(flags);
	}

}