package net.syntactickitsune.furblorb.cli.scrambling.impl;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.asset.ItemAsset;
import net.syntactickitsune.furblorb.cli.scrambling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.scrambling.ShuffleRandom;

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