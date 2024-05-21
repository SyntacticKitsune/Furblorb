package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;

/**
 * An {@link AssetShuffler} implementation that shuffles the IDs of a specific asset type.
 *
 * @author SyntacticKitsune
 *
 * @param <T> The type of asset the {@code AssetIDShuffler} shuffles.
 */
public abstract class AssetIDShuffler<T extends FurballAsset> implements AssetShuffler<T> {

	protected void shuffle(List<? extends FurballAsset> assets, ShuffleRandom rand) {
		record AssetName(String file, UUID id) {}

		final List<AssetName> names = new ArrayList<>(assets.size());

		for (FurballAsset asset : assets)
			names.add(new AssetName(asset.filename, asset.id));

		rand.shuffle(names);

		for (FurballAsset asset : assets) {
			final AssetName name = names.remove(0);
			asset.filename = name.file;
			asset.id = name.id;
		}
	}

	@Override
	public void shuffle(List<T> assets, ShuffleRandom rand, Furball furball) {
		shuffle(assets, rand);
	}
}