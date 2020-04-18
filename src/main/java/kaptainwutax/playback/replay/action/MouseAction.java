package kaptainwutax.playback.replay.action;

import net.minecraft.util.PacketByteBuf;

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
		((IMouseCaller)client.mouse).execute(this.action, this.d1, this.d2, this.i1);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		super.read(buf);

		this.action = ActionType.values()[buf.readVarInt()];

		if (this.action != ActionType.UPDATE) {
			this.d1 = buf.readDouble();
			this.d2 = buf.readDouble();

			if(this.action == ActionType.BUTTON) {
				i1 = buf.readVarInt();
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		super.write(buf);

		buf.writeVarInt(this.action.ordinal());

		if(action != ActionType.UPDATE) {
			buf.writeDouble(this.d1);
			buf.writeDouble(this.d2);

			if(this.action == ActionType.BUTTON) {
				buf.writeVarInt(this.i1);
			}
		}
	}

	public enum ActionType {
		POS, BUTTON, SCROLL, UPDATE
	}

	public interface IMouseCaller {
		void execute(ActionType action, double d1, double d2, int mods);
	}

}