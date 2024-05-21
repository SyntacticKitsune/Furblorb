package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.ItemAsset;

/**
 * An {@link AssetShuffler} that shuffles all {@linkplain ItemAsset items}.
 * @author SyntacticKitsune
 */
public final class Items extends AssetIDShuffler<ItemAsset> {

	@Override
	public Class<ItemAsset> assetType() { return ItemAsset.class; }

	@Override
	public String description() { return "Shuffles all of the items."; }

	@Override
	public void shuffle(List<ItemAsset> assets, ShuffleRandom rand, Furball furball) {
		// We need to avoid shuffling e.g. usable items into equipment slots.
		final Map<ItemAsset.Type, List<ItemAsset>> typeToItem = new EnumMap<>(ItemAsset.Type.class);

		for (ItemAsset asset : assets)
			typeToItem.computeIfAbsent(asset.type, k -> new ArrayList<>()).add(asset);

		for (var entry : typeToItem.entrySet())
			shuffle(entry.getValue(), rand);
	}
}