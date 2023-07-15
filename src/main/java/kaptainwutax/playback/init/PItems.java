package kaptainwutax.playback.init;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.item.FeatureStick;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PItems {

	public static final Item FEATURE_STICK = new FeatureStick(new Item.Settings().maxCount(1));

	public static void registerItems() {
		Registry.register(Registries.ITEM, Playback.createIdentifier("feature_stick"), FEATURE_STICK);
	}

}
