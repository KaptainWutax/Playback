package kaptainwutax.playback.init;

import kaptainwutax.playback.Playback;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyBindings {

	public static Set<FabricKeyBinding> KEYS = new HashSet<>();

	public static FabricKeyBinding TOGGLE_VIEW = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_view"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.playback").build();

	static {
		KEYS.add(TOGGLE_VIEW);
	}

	public static void registerKeyCategories() {
		KeyBindingRegistry.INSTANCE.addCategory("key.categories.playback");
	}

	public static void registerKeyBindings() {
		KeyBindingRegistry.INSTANCE.register(TOGGLE_VIEW);
	}

	public static boolean hasKeyCode(int keyCode) {
		for(FabricKeyBinding key: KEYS) {
			if(key.getBoundKey().getKeyCode() == keyCode) {
				return true;
			}
		}

		return false;
	}

}
