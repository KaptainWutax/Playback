package kaptainwutax.playback.capture.action;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyBindingAction implements IAction {

	public static final KeyInfo NO_KEY = new KeyInfo();

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

	private Map<String, KeyInfo> recordedKeys = new HashMap<>();
	private Map<String, KeyInfo> playKeys = new HashMap<>();

	public KeyBindingAction() {

	}

	private KeyInfo getAndSetKeyInfo(KeyBinding key) {
		if(key == null || BLACKLIST.contains(key.getId()))return NO_KEY;

		if(!this.recordedKeys.containsKey(key.getId())) {
			this.recordedKeys.put(key.getId(), new KeyInfo());
		}

		return this.recordedKeys.get(key.getId());
	}

	public void setKeyPressed(KeyBinding key, boolean pressed) {
		if(key == null)return;
		this.getAndSetKeyInfo(key).setKeyPressed(pressed);
	}

	public void onKeyPressed(KeyBinding key) {
		if(key == null)return;
		this.getAndSetKeyInfo(key).onKeyPressed();
	}

	public void reset(KeyBinding key) {
		this.getAndSetKeyInfo(key).reset();
	}

	public void consumeWasPressed(KeyBinding key) {
		this.getAndSetKeyInfo(key).consumeWasPressed();
	}

	public KeyBindingAction copy() {
		KeyBindingAction action = new KeyBindingAction();

		for(Map.Entry<String, KeyInfo> e: this.recordedKeys.entrySet()) {
			action.recordedKeys.put(e.getKey(), e.getValue().copy());
		}

		return action;
	}

	@Override
	public void play() {
		this.playKeys.clear();

		for(Map.Entry<String, KeyInfo> e: this.recordedKeys.entrySet()) {
			this.playKeys.put(e.getKey(), e.getValue().copy());
		}
	}

	public KeyInfo getPlayKey(KeyBinding key) {
		return this.playKeys.getOrDefault(key.getId(), NO_KEY);
	}

	public static class KeyInfo {
		public boolean pressed;
		public int timesPressed;

		public void setKeyPressed(boolean pressed) {
			this.pressed = pressed;
		}

		public void onKeyPressed() {
			++this.timesPressed;
		}

		public void reset() {
			this.setKeyPressed(false);
			this.timesPressed = 0;
		}

		public void consumeWasPressed() {
			this.wasPressed();
		}

		public boolean isPressed() {
			return this.pressed;
		}

		public boolean wasPressed() {
			if(this.timesPressed == 0) {
				return false;
			} else {
				--this.timesPressed;
				return true;
			}
		}

		public KeyInfo copy() {
			KeyInfo keyInfo = new KeyInfo();
			keyInfo.pressed = this.pressed;
			keyInfo.timesPressed = this.timesPressed;
			return keyInfo;
		}

	}

}