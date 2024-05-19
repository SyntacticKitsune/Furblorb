package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.ItemAsset;

/**
 * An {@link AssetShuffler} that shuffles the flavor text of all {@linkplain ItemAsset items}.
 * @author SyntacticKitsune
 */
public final class ItemFlavorText implements AssetShuffler<ItemAsset> {

	@Override
	public Class<ItemAsset> assetType() { return ItemAsset.class; }

	@Override
	public String description() { return "Shuffle the flavor text of all items"; }

	@Override
	public void shuffle(List<ItemAsset> assets, ShuffleRandom rand, Furball furball) {
		final List<String> descriptions = new ArrayList<>(assets.size());

		for (ItemAsset i : assets)
			descriptions.add(i.flavorText);

		rand.shuffle(descriptions);

		for (ItemAsset i : assets)
			i.flavorText = descriptions.remove(0);
	}
}