package net.syntactickitsune.furblorb.io;

import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.FurballSerializables.Metadata;

/**
 * Represents something that can be serialized using {@link FurballSerializables}.
 * Things implementing this {@code interface} are almost always annotated with {@link RegisterSerializable}.
 * @author SyntacticKitsune
 */
public interface IFurballSerializable {

	/**
	 * <p>
	 * Writes this serializable's data to the given {@code Encoder}.
	 * </p>
	 * <p>
	 * It is generally preferable to use {@link #writeWithId(Encoder)}, since the type may not be easily recoverable at deserialization time.
	 * </p>
	 * @param to The {@code Encoder} to write the data to.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to);

	/**
	 * <p>
	 * Writes this serializable's data to the given {@code Encoder}, prefixed with the serializable's type or ID.
	 * </p>
	 * <p>
	 * This is almost always the method one should use to serialize {@code IFurballSerializables}.
	 * </p>
	 * @param to The {@code Encoder} to write the data to.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public default void writeWithId(Encoder to) {
		if (to.writeCompressedTypes())
			to.writeInt("!Type", metadata().id());
		else
			to.writeString("!Type", metadata().name());

		write(to);
	}

	/**
	 * Convenience method to call {@link FurballSerializables#lookupByClass(Class)}.
	 * @return The metadata associated with this {@code class}.
	 */
	public default Metadata<?> metadata() {
		return FurballSerializables.lookupByClass(getClass());
	}
}