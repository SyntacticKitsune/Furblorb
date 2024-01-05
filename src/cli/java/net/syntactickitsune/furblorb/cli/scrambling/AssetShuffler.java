package net.syntactickitsune.furblorb.cli.scrambling;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.asset.SceneAsset;
import net.syntactickitsune.furblorb.api.asset.SceneNode;

/**
 * Represents an individual "shuffler" for Furblorb's asset shuffling API.
 *
 * @author SyntacticKitsune
 *
 * @param <T> The type of asset the {@code AssetShuffler} actually shuffles.
 */
public interface AssetShuffler<T extends FurballAsset> {

	public Class<T> assetType();
	public String description();

	public void shuffle(List<T> assets, ShuffleRandom rand, Furball furball);

	/**
	 * Using the provided list of scenes, returns a "flattened" list containing every {@link SceneNode}.
	 * @param scenes The list of scenes.
	 * @return The list of {@code SceneNode}s.
	 */
	public static List<SceneNode> discoverNodes(List<SceneAsset> scenes) {
		final List<SceneNode> ret = new ArrayList<>();

		final List<SceneNode> queue = new ArrayList<>();
		queue.addAll(scenes.stream().map(sa -> sa.root).toList());

		while (!queue.isEmpty()) {
			final SceneNode node = queue.remove(0);
			ret.add(node);

			queue.addAll(node.children);
		}

		return ret;
	}
}