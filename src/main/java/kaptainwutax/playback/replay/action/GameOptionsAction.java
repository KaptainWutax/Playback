package kaptainwutax.playback.replay.action;

import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public class GameOptionsAction extends Action {

	private Map<String, String> keyData = new HashMap<>();

	public GameOptionsAction() {

	}

	public GameOptionsAction(GameOptions options) {
		for(KeyBinding key: options.keysAll) {
			this.keyData.put(key.getId(), ((KeyCodeAccessor)key).getKeyCode().getName());
		}
	}

	@Override
	public void play() {
		for(KeyBinding key: MinecraftClient.getInstance().options.keysAll) {
			String keyCodeName = this.keyData.get(key.getId());
			if(keyCodeName == null)continue;
			MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.fromName(keyCodeName));
		}
	}

	@Override
	public Type getType() {
		return Type.GAME_OPTIONS;
	}

	@Override
	public void read(PacketByteBuf buf) {
		int size = buf.readVarInt();

		for(int i = 0; i < size; i++) {
			this.keyData.put(buf.readString(), buf.readString());
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.keyData.size());

		this.keyData.forEach((k, v) -> {
			buf.writeString(k);
			buf.writeString(v);
		});
	}

}
