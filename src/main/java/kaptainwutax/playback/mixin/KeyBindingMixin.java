package kaptainwutax.playback.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kaptainwutax.playback.replay.capture.PlayGameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements PlayGameOptions.IKeyBindingCaller {

	@Mutable @Shadow @Final private static Map<String, KeyBinding> keysById;
	@Mutable @Shadow @Final private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;
	@Mutable @Shadow @Final private static Set<String> keyCategories;
	@Mutable @Shadow @Final private static Map<String, Integer> categoryOrderMap;

	@Override
	public void resetStaticCollections() {
		keysById = Maps.newHashMap();
		keysByCode = Maps.newHashMap();
		keyCategories = Sets.newHashSet();

		categoryOrderMap = Util.make(Maps.newHashMap(), (hashMap) -> {
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
	public void setStaticCollections(Map<String, KeyBinding> keysById, Map<InputUtil.KeyCode, KeyBinding> keysByCode,
	                                 Set<String> keyCategories, Map<String, Integer> categoryOrderMap) {
		KeyBindingMixin.keysById = keysById;
		KeyBindingMixin.keysByCode = keysByCode;
		KeyBindingMixin.keyCategories = keyCategories;
		KeyBindingMixin.categoryOrderMap = categoryOrderMap;
	}

	@Override
	public Map<String, KeyBinding> getKeysById() {
		return keysById;
	}

	@Override
	public Map<InputUtil.KeyCode, KeyBinding> getKeysByCode() {
		return keysByCode;
	}

	@Override
	public Set<String> getKeyCategories() {
		return keyCategories;
	}

	@Override
	public Map<String, Integer> getCategoryOrderMap() {
		return categoryOrderMap;
	}

}
