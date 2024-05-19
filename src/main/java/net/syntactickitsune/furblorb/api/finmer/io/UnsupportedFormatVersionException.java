package net.syntactickitsune.furblorb.api.finmer.io;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;

/**
 * A {@link FurblorbParsingException} thrown when a furball is read or written with an unsupported format version.
 * @author SyntacticKitsune
 */
public final class UnsupportedFormatVersionException extends FurblorbParsingException {

	private final byte formatVersion;

	/**
	 * Constructs a new {@code FurballFormatException} with the specified values.
	 * @param formatVersion The format version of the offending furball.
	 * @param message The exception detail message.
	 */
	public UnsupportedFormatVersionException(byte formatVersion, @Nullable String message) {
		super(message);
		this.formatVersion = formatVersion;
	}

	/**
	 * @return The format version of the offending furball.
	 */
	public byte formatVersion() {
		return formatVersion;
	}
}