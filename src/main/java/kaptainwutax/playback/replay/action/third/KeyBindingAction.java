package kaptainwutax.playback.replay.action.third;

import com.google.common.collect.ImmutableSet;
import kaptainwutax.playback.init.KeyBindings;
import kaptainwutax.playback.replay.action.Action;
import net.minecraft.client.options.KeyBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyBindingAction extends Action {

	public static final KeyInfo NO_KEY = new KeyInfo();

	private static final Set<String> BLACKLIST = ImmutableSet.of(
			client.options.keyInventory.getId(),
			client.options.keyChat.getId(),
			client.options.keyPlayerList.getId(),
			client.options.keyCommand.getId(),
			client.options.keyScreenshot.getId(),
			client.options.keyTogglePerspective.getId(),
			client.options.keySmoothCamera.getId(),
			client.options.keyFullscreen.getId(),
			client.options.keySpectatorOutlines.getId(),
			client.options.keyAdvancements.getId(),
			KeyBindings.TOGGLE_VIEW.getId()
	);

	private Map<String, KeyInfo> recordedKeys = new HashMap<>();
	private Map<String, KeyInfo> playKeys = new HashMap<>();

	public KeyBindingAction() {

	}

	private KeyInfo getAndSetKeyInfo(KeyBinding key) {
		if(key == null || BLACKLIST.contains(key.getId())) return NO_KEY;

		if(!this.recordedKeys.containsKey(key.getId())) {
			this.recordedKeys.put(key.getId(), new KeyInfo());
		}

		return this.recordedKeys.get(key.getId());
	}

	public void setKeyPressed(KeyBinding key, boolean pressed) {
		if(key == null) return;
		this.getAndSetKeyInfo(key).setKeyPressed(pressed);
	}

	public void onKeyPressed(KeyBinding key) {
		if(key == null) return;
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

		for(Map.Entry<String, KeyInfo> e : this.recordedKeys.entrySet()) {
			action.recordedKeys.put(e.getKey(), e.getValue().copy());
		}

		return action;
	}

	@Override
	public void play() {
		this.playKeys.clear();

		for(Map.Entry<String, KeyInfo> e : this.recordedKeys.entrySet()) {
			this.playKeys.put(e.getKey(), e.getValue().copy());
		}
	}

	public void playUnpressAll() {
		//this.playKeys.values().forEach(KeyInfo::reset);
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