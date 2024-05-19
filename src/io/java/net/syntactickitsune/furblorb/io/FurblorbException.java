package net.syntactickitsune.furblorb.io;

import org.jetbrains.annotations.Nullable;

/**
 * A general exception thrown by various parts of Furblorb's API.
 * @author SyntacticKitsune
 * @see FurblorbParsingException
 */
public class FurblorbException extends RuntimeException {

	/**
	 * Constructs a new {@code FurblorbException} with no cause and {@code null} as its detail message.
	 */
	public FurblorbException() {}

	/**
	 * Constructs a new {@code FurblorbException} with the specified detail message.
	 * @param message The detail message. May be {@code null}.
	 */
	public FurblorbException(@Nullable String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code FurblorbException} with the specified detail message and cause.
	 * @param message The detail message. May be {@code null}.
	 * @param cause The cause. May be {@code null}.
	 */
	public FurblorbException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@code FurblorbException} with the specified cause.
	 * The detail message is inherited from the given {@code Throwable}, if non-{@code null}.
	 * @param cause The cause. May be {@code null}.
	 */
	public FurblorbException(@Nullable Throwable cause) {
		super(cause);
	}
}