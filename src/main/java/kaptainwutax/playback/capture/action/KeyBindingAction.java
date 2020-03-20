package kaptainwutax.playback.capture.action;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Set;

public class KeyBindingAction implements IAction {

	private static final Set<String> BLACKLIST = ImmutableSet.of(
			MinecraftClient.getInstance().options.keyInventory.getId(),
			MinecraftClient.getInstance().options.keyChat.getId(),
			MinecraftClient.getInstance().options.keyPlayerList.getId(),
			MinecraftClient.getInstance().options.keyCommand.getId(),
			MinecraftClient.getInstance().options.keyScreenshot.getId(),
			MinecraftClient.getInstance().options.keyTogglePerspective.getId(),
			MinecraftClient.getInstance().options.keySmoothCamera.getId(),
			MinecraftClient.getInstance().options.keyFullscreen.getId(),
			MinecraftClient.getInstance().options.keySpectatorOutlines.getId(),
			MinecraftClient.getInstance().options.keyAdvancements.getId()
	);

	private String keyId;
	private boolean state;

	public KeyBindingAction(KeyBinding key, boolean state) {
		this.keyId = key.getId();
		this.state = state;
	}

	public boolean isValid() {
		return !BLACKLIST.contains(this.keyId);
	}

	@Override
	public void play() {
		KeyBinding someRandomKey = MinecraftClient.getInstance().options.keySneak;
		KeyBinding.setKeyPressed(((IPublicKeys)someRandomKey).getKeyCode(this.keyId), this.state);
	}

	public interface IPublicKeys {

		InputUtil.KeyCode getKeyCode(String keyId);

		InputUtil.KeyCode getKeyCode();

	}

}