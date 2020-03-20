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

	public static final int ON_KEY_PRESSED = 0;
	public static final int SET_KEY_PRESSED = 1;

	private int action;
	private String keyId;
	private boolean state;

	public KeyBindingAction(int action, KeyBinding key, boolean state) {
		this.action = action;
		this.keyId = key == null ? null : key.getId();
		this.state = state;
	}

	public boolean isValid() {
		return this.keyId != null && !BLACKLIST.contains(this.keyId);
	}

	@Override
	public void play() {
		KeyBinding someRandomKey = MinecraftClient.getInstance().options.keySneak;

		if(this.action == SET_KEY_PRESSED) {
			KeyBinding.setKeyPressed(((IPublicKeys)someRandomKey).getKeyCode(this.keyId), this.state);
		} else if(this.action == ON_KEY_PRESSED) {
			KeyBinding.onKeyPressed(((IPublicKeys)someRandomKey).getKeyCode(this.keyId));
		}
	}

	public interface IPublicKeys {

		InputUtil.KeyCode getKeyCode(String keyId);

		InputUtil.KeyCode getKeyCode();

	}

}