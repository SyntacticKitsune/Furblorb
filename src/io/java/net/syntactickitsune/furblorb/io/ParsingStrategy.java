package net.syntactickitsune.furblorb.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for {@code enums} which determines how large to read/write ordinals.
 * This is used to override whatever the automatic detection believes is correct.
 * In practice this happens most commonly with {@code enums} that are written as {@code ints} instead of {@code bytes}.
 * @author SyntacticKitsune
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParsingStrategy {

	/**
	 * @return The {@code NumberType} that should be used for reading and writing this {@code enum}'s ordinals.
	 */
	public NumberType value();

	/**
	 * Represents the numeric data types that {@code enum} ordinals can be written as.
	 * @author SyntacticKitsune
	 */
	public static enum NumberType {

		/**
		 * The {@code enum} ordinal should be truncated to {@code byte} size.
		 */
		BYTE,

		/**
		 * The {@code enum} ordinal should be truncated to {@code short} size (2 {@code byte}s).
		 */
		SHORT,

		/**
		 * The {@code enum} ordinal should be left at {@code int} size.
		 */
		INT
	}
}