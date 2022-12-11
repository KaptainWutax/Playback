package kaptainwutax.playback.init;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PKeyBindings {

	public static String PLAYBACK_CATEGORY = "key.categories.playback";

	public static KeyBinding TOGGLE_VIEW = new KeyBinding("key.playback.toggle_view", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, PLAYBACK_CATEGORY);
	public static KeyBinding TOGGLE_PAUSE = new KeyBinding("key.playback.toggle_pause", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, PLAYBACK_CATEGORY);
	public static KeyBinding PLAY_CAMERA_PATH = new KeyBinding("key.playback.play_camera_path", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, PLAYBACK_CATEGORY);
	public static KeyBinding OPEN_REPLAY_HUD = new KeyBinding("key.playback.open_replay_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, PLAYBACK_CATEGORY);
	public static KeyBinding RENDER = new KeyBinding("key.playback.render", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, PLAYBACK_CATEGORY);

	public static KeyBinding[] SHARED_KEYBINDINGS = {TOGGLE_VIEW};

	public static void registerKeyBindings() {
		KeyBindingHelper.registerKeyBinding(TOGGLE_VIEW);
		KeyBindingHelper.registerKeyBinding(TOGGLE_PAUSE);
		KeyBindingHelper.registerKeyBinding(PLAY_CAMERA_PATH);
		KeyBindingHelper.registerKeyBinding(OPEN_REPLAY_HUD);
		KeyBindingHelper.registerKeyBinding(RENDER);
	}

	public static void updateSharedKeybindings(int keyCode, int scanCode, int i) {
		for(KeyBinding keyBinding : SHARED_KEYBINDINGS) {
			if (keyBinding.matchesKey(keyCode, scanCode)) {
				boolean pressed = i == 1;
				keyBinding.setPressed(pressed);
				if (pressed) {
					((IKeyBindingCaller)keyBinding).incrTimesPressed();
				}
			}
		}
	}

	public interface IKeyBindingCaller {
		void incrTimesPressed();
	}

}
