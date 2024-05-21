package net.syntactickitsune.furblorb.cli.shuffling.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.asset.StringTableAsset;

/**
 * An {@link AssetShuffler} that shuffles the contents of all {@linkplain StringTableAsset string tables} (or at least, the ones that won't break).
 * @author SyntacticKitsune
 */
public final class StringTables implements AssetShuffler<StringTableAsset> {

	@Override
	public Class<StringTableAsset> assetType() { return StringTableAsset.class; }

	@Override
	public String description() { return "Shuffle all string tables (that can be safely shuffled)"; }

	@Override
	public String category() { return "Text"; }

	@Override
	public void shuffle(List<StringTableAsset> assets, ShuffleRandom rand, Furball furball) {
		final List<List<String>> messable = new ArrayList<>();

		// Finmer'); DROP TABLE Strings;--
		for (StringTableAsset table : assets) {
			for (List<String> texts : table.table.values())
				if (noRegretsScramblingThis(texts))
					messable.add(texts);
		}

		final List<String> things = new ArrayList<>(messable.size());

		for (List<String> texts : messable)
			things.addAll(texts);

		rand.shuffle(things);

		for (List<String> texts : messable)
			for (int i = 0; i < texts.size(); i++)
				texts.set(i, things.remove(0));
	}

	private static final Pattern SUBSTITUTIONS = Pattern.compile("\\{\\^?([^\\}]+)\\}");
	private static final Set<String> ACTUALLY_FINE = Set.of("player.fur", "player.species", "player.name", "player.furry",
			"player.object", "player.speciesplural", "player.subject3p", "player.possessive3p");

	private static boolean noRegretsScramblingThis(List<String> texts) {
		for (String text : texts) {
			final Matcher m = SUBSTITUTIONS.matcher(text);
			while (m.find()) {
				final String sub = m.group(1);

				if (ACTUALLY_FINE.contains(sub))
					continue;

				//System.out.println("Not scrambling: " + sub);
				return false; // Regrets will be had.
			}
		}

		return true;
	}
}