package net.syntactickitsune.furblorb.api.finmer.io;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;

/**
 * An exception thrown when attempting to read an unknown {@link IFurballSerializable}.
 * That is, when the id or type does not point to any known {@code IFurballSerializable}.
 * @author SyntacticKitsune
 */
public final class UnknownSerializableException extends FurblorbParsingException {

	/**
	 * The id that was looked up. A {@code null} value indicates that a {@linkplain #type} was used instead.
	 */
	@Nullable
	public final Integer id;

	/**
	 * The type (or C# class name) that was looked up. A {@code null} value indicates that an {@linkplain #id ID} was used instead.
	 */
	@Nullable
	public final String type;

	/**
	 * Constructs a new {@code UnknownSerializableException} with the given looked up ID.
	 * @param id The ID that was looked up.
	 */
	public UnknownSerializableException(int id) {
		super("Unknown id: " + id);
		this.id = id;
		type = null;
	}

	/**
	 * Constructs a new {@code UnknownSerializableException} with the given looked up type (i.e. C# class name).
	 * @param type The type that was looked up.
	 */
	public UnknownSerializableException(String type) {
		super("Unknown type: " + type);
		id = null;
		this.type = type;
	}
}