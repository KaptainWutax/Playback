package kaptainwutax.playback.init;

import kaptainwutax.playback.Playback;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PKeyBindings {

	public static String PLAYBACK_CATEGORY = createCategory("playback");

	public static FabricKeyBinding TOGGLE_VIEW = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_view"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, PLAYBACK_CATEGORY).build();
	public static FabricKeyBinding TOGGLE_PAUSE = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_pause"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, PLAYBACK_CATEGORY).build();
	public static FabricKeyBinding PLAY_CAMERA_PATH = FabricKeyBinding.Builder.create(Playback.createIdentifier("play_camera_path"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, PLAYBACK_CATEGORY).build();
	public static FabricKeyBinding OPEN_REPLAY_HUD = FabricKeyBinding.Builder.create(Playback.createIdentifier("open_replay_hud"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, PLAYBACK_CATEGORY).build();
	public static FabricKeyBinding RENDER = FabricKeyBinding.Builder.create(Playback.createIdentifier("render"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, PLAYBACK_CATEGORY).build();

	public static FabricKeyBinding[] SHARED_KEYBINDINGS = {TOGGLE_VIEW};

	public static String createCategory(String name) {
		return "key.categories." + name;
	}

	public static void registerKeyCategories() {
		KeyBindingRegistry.INSTANCE.addCategory(PLAYBACK_CATEGORY);
	}

	public static void registerKeyBindings() {
		KeyBindingRegistry.INSTANCE.register(TOGGLE_VIEW);
		KeyBindingRegistry.INSTANCE.register(TOGGLE_PAUSE);
		KeyBindingRegistry.INSTANCE.register(PLAY_CAMERA_PATH);
		KeyBindingRegistry.INSTANCE.register(OPEN_REPLAY_HUD);
		KeyBindingRegistry.INSTANCE.register(RENDER);

	}

	public static void updateSharedKeybindings(int keyCode, int scanCode, int i) {
		for (FabricKeyBinding keyBinding : SHARED_KEYBINDINGS) {
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
