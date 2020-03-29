package kaptainwutax.playback.replay.action.first;

import kaptainwutax.playback.replay.action.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;

public class F5ModeFixAction extends Action {

	private int perspectiveF5mode;

	public F5ModeFixAction() {}

	public F5ModeFixAction(int perspectiveF5mode) {
		this.perspectiveF5mode = perspectiveF5mode;
	}

	@Override
	public void play() {
		MinecraftClient.getInstance().options.perspective = this.perspectiveF5mode;
	}

	@Override
	public Type getType() {
		return Type.F5_FIX;
	}

	@Override
	public void read(PacketByteBuf buf) {
		perspectiveF5mode = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(perspectiveF5mode);
	}
}