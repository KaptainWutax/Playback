package kaptainwutax.playback.init;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.item.FeatureStick;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class PItems {

	public static final Item FEATURE_STICK = new FeatureStick(new Item.Settings().group(null).maxCount(1));

	public static void registerItems() {
		Registry.register(Registry.ITEM, Playback.createIdentifier("feature_stick"), FEATURE_STICK);
	}

}
