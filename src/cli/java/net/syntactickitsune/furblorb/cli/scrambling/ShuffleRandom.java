package net.syntactickitsune.furblorb.cli.scrambling;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * An extended version of {@link RandomGenerator} with support for shuffling lists.
 * @author SyntacticKitsune
 */
public sealed interface ShuffleRandom extends RandomGenerator permits ShuffleRandomImpl {

	/**
	 * See {@link Collections#shuffle(List, Random)}.
	 * @param list The list to shuffle.
	 */
	public void shuffle(List<?> list);

	/**
	 * Creates a new {@code ShuffleRandom} using the provided {@code RandomGenerator} as its source.
	 * @param random The source for the created {@code ShuffleRandom}.
	 * @return A new {@code ShuffleRandom}.
	 * @throws NullPointerException If {@code random} is {@code null}.
	 */
	public static ShuffleRandom of(RandomGenerator random) {
		return new ShuffleRandomImpl(Objects.requireNonNull(random));
	}
}