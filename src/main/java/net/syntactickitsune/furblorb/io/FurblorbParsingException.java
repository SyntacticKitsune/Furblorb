package net.syntactickitsune.furblorb.io;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link FurblorbException} thrown when something goes awry involving Furblorb I/O, such as when parsing malformed furballs.
 * @author SyntacticKitsune
 */
public class FurblorbParsingException extends FurblorbException {

	/**
	 * Constructs a new {@code FurblorbParsingException} with no cause and {@code null} as its detail message.
	 */
	public FurblorbParsingException() {}

	/**
	 * Constructs a new {@code FurblorbParsingException} with the specified detail message.
	 * @param message The detail message. May be {@code null}.
	 */
	public FurblorbParsingException(@Nullable String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code FurblorbParsingException} with the specified detail message and cause.
	 * @param message The detail message. May be {@code null}.
	 * @param cause The cause. May be {@code null}.
	 */
	public FurblorbParsingException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@code FurblorbParsingException} with the specified cause.
	 * The detail message is inherited from the given {@code Throwable}, if non-{@code null}.
	 * @param cause The cause. May be {@code null}.
	 */
	public FurblorbParsingException(@Nullable Throwable cause) {
		super(cause);
	}
}