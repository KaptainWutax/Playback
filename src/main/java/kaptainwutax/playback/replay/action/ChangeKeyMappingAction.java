package kaptainwutax.playback.replay.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class ChangeKeyMappingAction extends Action {

	private String keyId;
	private String keyCodeId;

	public ChangeKeyMappingAction() {

	}

	public ChangeKeyMappingAction(KeyBinding key, InputUtil.KeyCode keyCode) {
		this.keyId = key.getId();
		this.keyCodeId = keyCode.getName();
	}

	@Override
	public void play() {
		for(KeyBinding key: MinecraftClient.getInstance().options.keysAll) {
			if(key.getId().equals(this.keyId)) {
				MinecraftClient.getInstance().options.setKeyCode(key, InputUtil.fromName(this.keyCodeId));
				break;
			}
		}
	}

	@Override
	public Type getType() {
		return Type.CHANGE_KEY_MAPPING;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.keyId = buf.readString();
		this.keyCodeId = buf.readString();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.keyId);
		buf.writeString(this.keyCodeId);
	}

}
