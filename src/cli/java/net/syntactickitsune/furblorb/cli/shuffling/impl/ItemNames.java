package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.asset.ItemAsset;
import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;

/**
 * An {@link AssetShuffler} that shuffles the names of all {@linkplain ItemAsset items}.
 * @author SyntacticKitsune
 */
public final class ItemNames implements AssetShuffler<ItemAsset> {

	@Override
	public Class<ItemAsset> assetType() { return ItemAsset.class; }

	@Override
	public String description() { return "Shuffle the names of all items"; }

	@Override
	public void shuffle(List<ItemAsset> assets, ShuffleRandom rand, Furball furball) {
		record Name(String name, String alias) {}

		final List<Name> names = new ArrayList<>(assets.size());

		for (ItemAsset i : assets)
			names.add(new Name(i.objectName, i.objectAlias));

		rand.shuffle(names);

		for (ItemAsset i : assets) {
			final Name name = names.remove(0);
			i.objectName = name.name;
			i.objectAlias = name.alias;
		}
	}
}