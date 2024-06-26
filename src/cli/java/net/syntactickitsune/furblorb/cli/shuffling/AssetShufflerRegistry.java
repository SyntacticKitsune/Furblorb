package net.syntactickitsune.furblorb.cli.shuffling;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import net.syntactickitsune.furblorb.cli.shuffling.impl.Creatures;
import net.syntactickitsune.furblorb.cli.shuffling.impl.ItemFlavorText;
import net.syntactickitsune.furblorb.cli.shuffling.impl.ItemNames;
import net.syntactickitsune.furblorb.cli.shuffling.impl.Items;
import net.syntactickitsune.furblorb.cli.shuffling.impl.SceneChoiceText;
import net.syntactickitsune.furblorb.cli.shuffling.impl.StringTables;

/**
 * Tracks every known {@link AssetShuffler} so that the CLI can actually invoke them.
 * @author SyntacticKitsune
 */
public final class AssetShufflerRegistry {

	private static final Map<String, AssetShuffler<?>> REGISTRY = new TreeMap<>();

	/**
	 * Registers the specified {@code AssetShuffler}.
	 * @param key The name to associate the shuffler with.
	 * @param shuffler The shuffler to register.
	 */
	public static void register(String key, AssetShuffler<?> shuffler) {
		REGISTRY.put(key.toLowerCase(Locale.ENGLISH), shuffler);
	}

	/**
	 * Returns an unmodifiable view of the registry.
	 * @return An unmodifiable view of the registry.
	 */
	public static Map<String, AssetShuffler<?>> getRegistry() {
		return Collections.unmodifiableMap(REGISTRY);
	}

	/**
	 * Returns the {@code AssetShuffler} associated with the specified key, or {@code null} if no such shuffler exists.
	 * @param key The key to lookup.
	 * @return The {@code AssetShuffler} associated with the key.
	 */
	public static AssetShuffler<?> get(String key) {
		return REGISTRY.get(key);
	}

	static {
		register("item_flavor_text", new ItemFlavorText());
		register("item_names", new ItemNames());
		register("scene_choice_text", new SceneChoiceText());
		register("strings", new StringTables());
		register("creatures", new Creatures());
		register("items", new Items());
	}
}