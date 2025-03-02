package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.SceneAsset;
import net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode;

/**
 * An {@link AssetShuffler} that shuffles the titles and tooltips of all {@link SceneAsset}
 * {@linkplain net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode.Type#CHOICE choice nodes}.
 * @author SyntacticKitsune
 */
public final class SceneChoiceText implements AssetShuffler<SceneAsset> {

	@Override
	public Class<SceneAsset> assetType() { return SceneAsset.class; }

	@Override
	public String description() { return "Shuffle the titles and tooltips of all choices"; }

	@Override
	public String category() { return "Text"; }

	@Override
	public void shuffle(List<SceneAsset> assets, ShuffleRandom rand, Furball furball) {
		final List<SceneNode> interestingNodes = AssetShuffler.discoverNodes(assets)
				.stream()
				.filter(node -> !node.title.isEmpty())
				.toList();

		final List<String> titles = new ArrayList<>(interestingNodes.size());
		final List<String> tooltips = new ArrayList<>(interestingNodes.size());

		for (SceneNode node : interestingNodes) {
			titles.add(node.title);
			tooltips.add(node.tooltip);
		}

		rand.shuffle(titles);
		rand.shuffle(tooltips);

		for (SceneNode node : interestingNodes) {
			node.title = titles.remove(0);
			node.tooltip = tooltips.remove(0);
		}
	}
}