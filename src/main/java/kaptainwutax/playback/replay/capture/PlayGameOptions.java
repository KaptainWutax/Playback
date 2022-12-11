package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.init.PKeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class PlayGameOptions {

	private Map<String, KeyBinding> keysById;
	private Map<InputUtil.Key, KeyBinding> keysByCode;
	private Set<String> keyCategories;
	private Map<String, Integer> categoryOrderMap;

	private final IKeyBindingCaller dummyKey;
	private GameOptions options;

	public PlayGameOptions() {
		this(new GameOptions(MinecraftClient.getInstance(), null));
	}

	public PlayGameOptions(GameOptions options) {
		this.options = options;
		this.dummyKey = ((IKeyBindingCaller)this.options.allKeys[0]);
		this.keysById = this.dummyKey.getKeysById();
		this.keysByCode = this.dummyKey.getKeysByCode();
		this.keyCategories = this.dummyKey.getKeyCategories();
		this.categoryOrderMap = this.dummyKey.getCategoryOrderMap();
		this.dummyKey.resetStaticCollections();
	}

	public GameOptions getOptions() {
		return this.options;
	}

	public void apply() {
		((IClientCaller)MinecraftClient.getInstance()).setOptions(this.options);
		this.dummyKey.setStaticCollections(this.keysById, this.keysByCode, this.keyCategories, this.categoryOrderMap);
	}

	public static void loadContents(GameOptions options, String contents) {
		File file = new File("temp_options.txt");
		File actualFile = ((IOptionsCaller)options).getFile();

		try {
			((IOptionsCaller)options).setFile(file);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(contents);
			writer.flush();
			options.load();
			((IOptionsCaller)options).setFile(actualFile);
			writer.close();
			file.delete();
		} catch(IOException e) {
			e.printStackTrace();
			((IOptionsCaller)options).setFile(actualFile);
			file.delete();
		}
	}

	public static String getContents(GameOptions options) {
		File file = new File("temp_options.txt");
		File actualFile = ((IOptionsCaller)options).getFile();

		try {
			((IOptionsCaller)options).setFile(file);
			options.write();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder contents = new StringBuilder();

			while(reader.ready()) {
				contents.append(reader.readLine()).append("\n");
			}

			((IOptionsCaller)options).setFile(actualFile);
			reader.close();
			file.delete();
			return contents.toString();
		} catch(IOException e) {
			e.printStackTrace();
			((IOptionsCaller)options).setFile(actualFile);
			file.delete();
		}

		return "";
	}

	public boolean isActive() {
		return MinecraftClient.getInstance().options == this.options;
	}

	public interface IKeyBindingCaller {
		void resetStaticCollections();
		void setStaticCollections(Map<String, KeyBinding> keysById, Map<InputUtil.Key, KeyBinding> keysByCode,
		                          Set<String> keyCategories, Map<String, Integer> categoryOrderMap);

		Map<String, KeyBinding> getKeysById();

		Map<InputUtil.Key, KeyBinding> getKeysByCode();

		Set<String> getKeyCategories();

		Map<String, Integer> getCategoryOrderMap();
	}

	public interface IClientCaller {
		void setOptions(GameOptions options);
	}

	public interface IOptionsCaller {
		void setFile(File file);
		File getFile();
	}

}
