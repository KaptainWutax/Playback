package kaptainwutax.playback.replay.action;

import net.minecraft.util.PacketByteBuf;

public class KeyAction extends Action {

	private int action;
	private int key;
	private int scanCode;
	private int i;
	private int j;

	public KeyAction() {}

	public KeyAction(int action, int key, int scanCode, int i, int j) {
		this.action = action;
		this.key = key;
		this.scanCode = scanCode;
		this.i = i;
		this.j = j;
	}

	@Override
	public void play() {
		((IKeyboardCaller)client.keyboard).execute(this.action, this.key, this.scanCode, this.i, this.j);
	}

	@Override
	public Type getType() {
		return Type.KEY;
	}

	@Override
	public void read(PacketByteBuf buf) {
		action = buf.readVarInt();
		key = buf.readVarInt();
		scanCode = buf.readVarInt();
		i = buf.readVarInt();
		j = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(action);
		buf.writeVarInt(key);
		buf.writeVarInt(scanCode);
		buf.writeVarInt(i);
		buf.writeVarInt(j);
	}

	public interface IKeyboardCaller {
		void execute(int action, int key, int scanCode, int i, int j);
	}

}