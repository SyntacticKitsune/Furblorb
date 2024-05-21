package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.ItemAsset;

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
	public String category() { return "Text"; }

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