package net.syntactickitsune.furblorb.finmer.io;

import net.syntactickitsune.furblorb.io.FurblorbParsingException;

/**
 * A {@link FurblorbParsingException} thrown when attempting to write a furball that contains things that don't support the current format version.
 * For example, using game starts in scenes with format version &lt;20.
 * @author SyntacticKitsune
 */
public final class IllegalFormatVersionException extends FurblorbParsingException {

	private final byte requiredFormatVersion;
	private final byte providedFormatVersion;

	/**
	 * Constructs a new {@code IllegalFormatVersionException} with the specified values.
	 * @param requiredFormatVersion The format version required for the feature.
	 * @param providedFormatVersion The current (illegal) format version.
	 * @param message The feature or action in question.
	 */
	public IllegalFormatVersionException(byte requiredFormatVersion, byte providedFormatVersion, String message) {
		super("Cannot " + message + " in format version " + providedFormatVersion + " as it requires format version " + requiredFormatVersion);
		this.requiredFormatVersion = requiredFormatVersion;
		this.providedFormatVersion = providedFormatVersion;
	}

	/**
	 * @return The minimum required format version for the feature.
	 */
	public byte requiredFormatVersion() {
		return requiredFormatVersion;
	}

	/**
	 * @return The provided format version that is illegal for the feature.
	 */
	public byte providedFormatVersion() {
		return providedFormatVersion;
	}
}