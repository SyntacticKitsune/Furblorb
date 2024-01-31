package net.syntactickitsune.furblorb.api.io;

/**
 * A {@link FurblorbParsingException} thrown when attempting to write a furball that contains things that don't support the current format version.
 * For example, using game starts in scenes with format version &lt;20.
 * @author SyntacticKitsune
 */
public final class IllegalFormatVersionException extends FurblorbParsingException {

	private final byte requiredFormatVersion;
	private final byte providedFormatVersion;

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