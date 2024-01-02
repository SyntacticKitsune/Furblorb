package net.syntactickitsune.furblorb.api.util;

import java.util.UUID;

import net.syntactickitsune.furblorb.api.Furball;

/**
 * Various {@link Furball}-related utilities.
 * @author SyntacticKitsune
 */
public final class FurballUtil {

	/**
	 * Represents an "empty" {@code UUID}, or at least, what Finmer uses to mean one.
	 */
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
}