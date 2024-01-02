package net.syntactickitsune.furblorb.api.util;

import java.util.function.BiConsumer;

/**
 * Basically {@link BiConsumer} but with another argument.
 *
 * @author SyntacticKitsune
 *
 * @param <A> The type of the first argument to the operation.
 * @param <B> The type of the second argument to the operation.
 * @param <C> The type of the third argument to the operation.
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

	/**
	 * Performs this operation on the given arguments.
	 * @param a The first input argument.
	 * @param b The second input argument.
	 * @param c The third input argument.
	 */
	public void accept(A a, B b, C c);
}