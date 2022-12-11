package kaptainwutax.playback.mixin.client.options;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kaptainwutax.playback.init.PKeyBindings;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements PlayGameOptions.IKeyBindingCaller, PKeyBindings.IKeyBindingCaller {

	@Mutable @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;
	@Mutable @Shadow @Final private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
	@Mutable @Shadow @Final private static Set<String> KEY_CATEGORIES;
	@Mutable @Shadow @Final private static Map<String, Integer> CATEGORY_ORDER_MAP;

	@Shadow private int timesPressed;

	@Override
	public void resetStaticCollections() {
		KEYS_BY_ID = Maps.newHashMap();
		KEY_TO_BINDINGS = Maps.newHashMap();
		KEY_CATEGORIES = Sets.newHashSet();

		CATEGORY_ORDER_MAP = Util.make(Maps.newHashMap(), (hashMap) -> {
			hashMap.put("key.categories.movement", 1);
			hashMap.put("key.categories.gameplay", 2);
			hashMap.put("key.categories.inventory", 3);
			hashMap.put("key.categories.creative", 4);
			hashMap.put("key.categories.multiplayer", 5);
			hashMap.put("key.categories.ui", 6);
			hashMap.put("key.categories.misc", 7);
		});
	}

	@Override
	public void setStaticCollections(Map<String, KeyBinding> keysById, Map<InputUtil.Key, KeyBinding> keysByCode,
	                                 Set<String> keyCategories, Map<String, Integer> categoryOrderMap) {
		KeyBindingMixin.KEYS_BY_ID = keysById;
		KeyBindingMixin.KEY_TO_BINDINGS = keysByCode;
		KeyBindingMixin.KEY_CATEGORIES = keyCategories;
		KeyBindingMixin.CATEGORY_ORDER_MAP = categoryOrderMap;
	}

	@Override
	public Map<String, KeyBinding> getKeysById() {
		return KEYS_BY_ID;
	}

	@Override
	public Map<InputUtil.Key, KeyBinding> getKeysByCode() {
		return KEY_TO_BINDINGS;
	}

	@Override
	public Set<String> getKeyCategories() {
		return KEY_CATEGORIES;
	}

	@Override
	public Map<String, Integer> getCategoryOrderMap() {
		return CATEGORY_ORDER_MAP;
	}


	@Override
	public void incrTimesPressed() {
		this.timesPressed++;
	}
}
