package kaptainwutax.playback.replay.action;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;

public class MouseAction extends Action {

	private ActionType action;
	private double d1;
	private double d2;
	private int i1;

	private int screenPositionsMaxIndex;
	private double[] screenCoordinates;

	public MouseAction() {
		super(true);
		this.screenPositionsMaxIndex = -1;
	}

	public MouseAction(ActionType action, double d1, double d2, int i1) {
		this();
		this.action = action;
		this.d1 = d1;
		this.d2 = d2;
		this.i1 = i1;
	}

	public void addScreenPositionData(double screenCoord, int index) {
		if (this.screenCoordinates == null) {
			this.screenCoordinates = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		}
		if (this.screenCoordinates[index] != Double.NEGATIVE_INFINITY) {
			throw new IllegalStateException("Dataloss while recording mouse action. Can only save coordinate for index " + index + " once!");
		}
		this.screenCoordinates[index] = screenCoord;
		if (this.screenPositionsMaxIndex < index) {
			this.screenPositionsMaxIndex = index;
		}
	}

	public double getScreenPositionData(int index) {
		if (index > this.screenPositionsMaxIndex) {
			throw new IndexOutOfBoundsException("Reading screen position index " + index + ". Can only read coordinate at index from 0 up to " + this.screenPositionsMaxIndex + "!");
		}
		double ret = this.screenCoordinates[index];
		if (ret == Double.POSITIVE_INFINITY) //Positive infinity is a flag for already read
			throw new IndexOutOfBoundsException("Mouse action " + this.action.name() + " should only read value at index "+index+" once! This is unexpected");
		this.screenCoordinates[index] = Double.POSITIVE_INFINITY;
		return ret;
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

				this.screenPositionsMaxIndex = buf.readVarInt();
				if (this.screenPositionsMaxIndex >= 0) {
					this.screenCoordinates = new double[this.screenPositionsMaxIndex + 1];
					for (int i = 0; i <= this.screenPositionsMaxIndex; i++) {
						this.screenCoordinates[i] = buf.readDouble();
					}
				}
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

				buf.writeVarInt(this.screenPositionsMaxIndex);
				for (int i = 0; i <= this.screenPositionsMaxIndex; i++) {
					if (this.screenCoordinates[i] == Double.NEGATIVE_INFINITY) {
						System.err.println("Writing noninitialized values for MouseAction!");
					}
					buf.writeDouble(this.screenCoordinates[i]);
				}
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