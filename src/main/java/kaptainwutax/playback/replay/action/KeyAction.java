package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.util.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class KeyAction extends Action {

	private ActionType action;
	private int key;
	private int scanCode;
	private int i;
	private int j;

	public KeyAction() {
		super(false);
	}

	public KeyAction(ActionType action, int key, int scanCode, int i, int j) {
		this();
		this.action = action;
		this.key = key;
		this.scanCode = scanCode;
		this.i = i;
		this.j = j;
	}

	@Override
	public void play() {
		if(this.i == GLFW.GLFW_PRESS || i == GLFW.GLFW_RELEASE) {
			Playback.getManager().recording.setKeyState(this.key, this.i == GLFW.GLFW_PRESS);
		}

		((IKeyboardCaller)client.keyboard).execute(this.action, this.key, this.scanCode, this.i, this.j);
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.action = ActionType.values()[buf.readVarInt()];

		if (this.action == ActionType.KEY) {
			this.key = buf.readVarInt();
			this.scanCode = buf.readVarInt();
		}

		this.i = buf.readVarInt();
		this.j = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.action.ordinal());

		if(this.action == ActionType.KEY) {
			buf.writeVarInt(this.key);
			buf.writeVarInt(this.scanCode);
		}

		buf.writeVarInt(this.i);
		buf.writeVarInt(this.j);
	}

	public enum ActionType {
		KEY, CHAR
	}

	public interface IKeyboardCaller {
		void execute(ActionType action, int key, int scanCode, int i, int j);
	}

}
