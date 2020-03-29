package kaptainwutax.playback.replay.capture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Map;
import java.util.Set;

public class PlayGameOptions extends GameOptions {

	private Map<String, KeyBinding> keysById;
	private Map<InputUtil.KeyCode, KeyBinding> keysByCode;
	private Set<String> keyCategories;
	private Map<String, Integer> categoryOrderMap;

	private final IKeyBindingCaller dummyKey;

	public PlayGameOptions() {
		super(MinecraftClient.getInstance(), null);
		this.dummyKey = ((IKeyBindingCaller)this.keysAll[0]);
		this.keysById = this.dummyKey.getKeysById();
		this.keysByCode = this.dummyKey.getKeysByCode();
		this.keyCategories = this.dummyKey.getKeyCategories();
		this.categoryOrderMap = this.dummyKey.getCategoryOrderMap();
		this.dummyKey.resetStaticCollections();
	}

	public void apply() {
		this.dummyKey.setStaticCollections(this.keysById, this.keysByCode, this.keyCategories, this.categoryOrderMap);
	}

	public interface IKeyBindingCaller {
		void resetStaticCollections();
		void setStaticCollections(Map<String, KeyBinding> keysById, Map<InputUtil.KeyCode, KeyBinding> keysByCode,
		                          Set<String> keyCategories, Map<String, Integer> categoryOrderMap);

		Map<String, KeyBinding> getKeysById();

		Map<InputUtil.KeyCode, KeyBinding> getKeysByCode();

		Set<String> getKeyCategories();

		Map<String, Integer> getCategoryOrderMap();
	}

}
