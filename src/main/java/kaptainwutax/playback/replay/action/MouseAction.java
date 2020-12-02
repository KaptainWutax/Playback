package kaptainwutax.playback.replay.action;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

public class MouseAction extends Action {

	private ActionType action;
	private double d1;
	private double d2;
	private int i1;

	public MouseAction() {
		super(true);
	}

	public MouseAction(ActionType action, double d1, double d2, int i1) {
		this();
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
	}

	@Override
	public void play() {
		((IMouseCaller)client.mouse).execute(this.action, this, this.d1, this.d2, this.i1);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		super.read(buf);

		this.action = ActionType.values()[buf.readVarInt()];

		switch (this.action) {
			case BUTTON:
				this.i1 = buf.readVarInt();
				//fallthrough
			case POS:
			case SCROLL:
				this.d1 = buf.readDouble();
				this.d2 = buf.readDouble();
				break;
			case RESOLUTION_CHANGED:
			case UPDATE:
				break;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		super.write(buf);

		buf.writeVarInt(this.action.ordinal());
		switch (this.action) {
			case BUTTON:
				buf.writeVarInt(this.i1);
				//fallthrough
			case POS:
			case SCROLL:
				buf.writeDouble(this.d1);
				buf.writeDouble(this.d2);
				break;

			case RESOLUTION_CHANGED:
			case UPDATE:
				break;
		}
	}

	public enum ActionType {
		POS, BUTTON, SCROLL, UPDATE, RESOLUTION_CHANGED
	}

	public interface IMouseCaller {
		void execute(ActionType actionType, MouseAction action, double d1, double d2, int mods);
	}

}