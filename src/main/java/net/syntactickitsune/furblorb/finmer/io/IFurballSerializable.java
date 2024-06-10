package net.syntactickitsune.furblorb.finmer.io;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables.Metadata;
import net.syntactickitsune.furblorb.io.Encoder;

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
	 * Begins traversal of this {@code IFurballSerializable}'s component hierarchy using the provided visitor.
	 * </p>
	 * <p>
	 * By default this method only invokes {@link ISerializableVisitor#visitSerializable(IFurballSerializable) visitSerializable(IFurballSerializable)}
	 * (followed by {@link ISerializableVisitor#visitEnd() visitEnd()}).
	 * It is expected that subclasses will override this method to traverse each component, if applicable.
	 * </p>
	 * @param visitor The visitor to receive callback events for each visited object.
	 * @since 2.0.0
	 */
	public default void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this))
			visitor.visitEnd();
	}

	/**
	 * <p>
	 * Writes this serializable's data to the given {@code Encoder}, prefixed with the serializable's type or ID.
	 * </p>
	 * <p>
	 * This is almost always the method one should use to serialize an {@code IFurballSerializable}.
	 * </p>
	 * @param to The {@code Encoder} to write the data to.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public default void writeWithId(Encoder to) {
		final Metadata<?> meta = metadata();
		if (meta.minFormatVersion() > to.formatVersion())
			throw new IllegalFormatVersionException(meta.minFormatVersion(), to.formatVersion(), "serialize " + getClass().getSimpleName());
		if (meta.maxFormatVersion() < to.formatVersion())
			throw new IllegalFormatVersionException(meta.maxFormatVersion(), to.formatVersion(), "serialize " + getClass().getSimpleName());

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