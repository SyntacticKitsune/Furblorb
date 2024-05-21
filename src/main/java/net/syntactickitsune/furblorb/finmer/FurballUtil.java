package net.syntactickitsune.furblorb.finmer;

import java.util.UUID;

import net.syntactickitsune.furblorb.io.FurblorbParsingException;

/**
 * Various {@link Furball}-related utilities.
 * @author SyntacticKitsune
 */
public final class FurballUtil {

	/**
	 * Represents an "empty" {@code UUID}, or at least, what Finmer uses to mean one.
	 */
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static void checkInRange(String name, int value, int min, int max) {
		if (value < min || value > max)
			throw new FurblorbParsingException(name + " value out of range [" + min + ", " + max + "]: " + value);
	}
}