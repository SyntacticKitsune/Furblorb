package net.syntactickitsune.furblorb.cli.shuffling.impl;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.finmer.asset.CreatureAsset;

/**
 * An {@link AssetShuffler} that shuffles all {@linkplain CreatureAsset creatures}.
 * @author SyntacticKitsune
 */
public final class Creatures extends AssetIDShuffler<CreatureAsset> {

	@Override
	public Class<CreatureAsset> assetType() { return CreatureAsset.class; }

	@Override
	public String description() { return "Shuffles all of the creatures (or \"characters\") so that they appear in different battles"; }
}