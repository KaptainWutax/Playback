package kaptainwutax.playback.replay.action;

import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class MouseAction extends Action {

	private int action;
	private double d1;
	private double d2;
	private int i1;

	public MouseAction() {
		super(true);
	}

	public MouseAction(int action, double d1, double d2, int i1) {
		this();
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
	}

	@Override
	public void play() {
		((IMouseCaller)client.mouse).execute(this.action, this.d1, this.d2, this.i1);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		super.read(buf);

		action = buf.readVarInt();
		if (action != 3) {
			d1 = buf.readDouble();
			d2 = buf.readDouble();
			if (action == 1) {
				i1 = buf.readVarInt();
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		super.write(buf);

		buf.writeVarInt(action);
		if (action != 3) {
			buf.writeDouble(d1);
			buf.writeDouble(d2);
			if (action == 1) {
				buf.writeVarInt(i1);
			}
		}

	}

	public interface IMouseCaller {
		void execute(int action, double d1, double d2, int mods);
	}

}