package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import net.minecraft.util.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class KeyAction extends Action {

	private int action;
	private int key;
	private int scanCode;
	private int i;
	private int j;

	public KeyAction() {
		super(false);
	}

	public KeyAction(int action, int key, int scanCode, int i, int j) {
		this();
		this.action = action;
		this.key = key;
		this.scanCode = scanCode;
		this.i = i;
		this.j = j;
	}

	@Override
	public void play() {
		if (this.i == GLFW.GLFW_PRESS || i == GLFW.GLFW_RELEASE) {
			Playback.getManager().recording.setKeyState(this.key, this.i == GLFW.GLFW_PRESS);
		}
		((IKeyboardCaller)client.keyboard).execute(this.action, this.key, this.scanCode, this.i, this.j);
	}

	@Override
	public void read(PacketByteBuf buf) {
		action = buf.readVarInt();
		if (action != 1) {
			key = buf.readVarInt();
			scanCode = buf.readVarInt();
		}
		i = buf.readVarInt();
		j = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(action);
		if (action != 1) {
			buf.writeVarInt(key);
			buf.writeVarInt(scanCode);
		}
		buf.writeVarInt(i);
		buf.writeVarInt(j);
	}

	public interface IKeyboardCaller {
		void execute(int action, int key, int scanCode, int i, int j);
	}

}
