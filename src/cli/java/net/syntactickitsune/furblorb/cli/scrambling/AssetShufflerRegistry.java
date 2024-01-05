package net.syntactickitsune.furblorb.cli.scrambling;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import net.syntactickitsune.furblorb.cli.scrambling.impl.ItemFlavorText;
import net.syntactickitsune.furblorb.cli.scrambling.impl.ItemNames;
import net.syntactickitsune.furblorb.cli.scrambling.impl.SceneChoiceText;
import net.syntactickitsune.furblorb.cli.scrambling.impl.StringTables;

public final class AssetShufflerRegistry {

	private static final Map<String, AssetShuffler<?>> REGISTRY = new TreeMap<>();

	public static void register(String key, AssetShuffler<?> shuffler) {
		REGISTRY.put(key.toLowerCase(Locale.ENGLISH), shuffler);
	}

	public static Map<String, AssetShuffler<?>> getRegistry() {
		return Collections.unmodifiableMap(REGISTRY);
	}

	public static AssetShuffler<?> get(String key) {
		return REGISTRY.get(key);
	}

	static {
		register("itemflavortext", new ItemFlavorText());
		register("itemnames", new ItemNames());
		register("scenechoicetext", new SceneChoiceText());
		register("strings", new StringTables());
	}
}