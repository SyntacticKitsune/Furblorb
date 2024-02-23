package net.syntactickitsune.furblorb.api;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents the metadata associated with a {@link Furball}.
 * Most importantly, these are the format version and id, but there's also the title and author name.
 */
public final class FurballMetadata {

	/**
	 * The current oldest furball format version that Furblorb understands.
	 */
	public static final byte MINIMUM_VERSION = 19;

	/**
	 * The current latest furball format version that Furblorb understands.
	 */
	public static final byte LATEST_VERSION = 20;

	/**
	 * The format version the furball will be read and written using.
	 * This determines which features the furball can make use of,
	 * and overall indicates the relative "age" of the furball.
	 * It may be a minimum of 19 and a maximum of {@link #LATEST_VERSION}.
	 */
	public byte formatVersion = LATEST_VERSION;

	/**
	 * The unique identifier of the furball.
	 * This is used primarily by other furballs to depend on a given furball.
	 */
	public UUID id;

	/**
	 * The title or name of the furball.
	 */
	public String title;

	/**
	 * The name of the creator of the furball.
	 */
	public String author;

	/**
	 * Constructs a new {@code FurballMetadata} with default values.
	 */
	public FurballMetadata() {}

	/**
	 * Decodes the {@code FurballMetadata} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @param formatVersion The furball's format version.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public FurballMetadata(Decoder in, byte formatVersion) {
		this.formatVersion = formatVersion;
		id = in.readUUID("ID");
		title = in.readString("Title");
		author = in.readString("Author");
	}

	/**
	 * Writes this {@code FurballMetadata} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeByte("FormatVersion", formatVersion);
		to.writeUUID("ID", id);
		to.writeString("Title", title);
		to.writeString("Author", author);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof FurballMetadata fm)) return false;
		return formatVersion == fm.formatVersion && Objects.equals(id, fm.id)
				&& Objects.equals(title, fm.title) && Objects.equals(author, fm.author);
	}

	@Override
	public int hashCode() {
		return Objects.hash(formatVersion, id, title, author);
	}

	@Override
	public String toString() {
		return "\"" + title + "\" by " + author + " (" + id + ") format " + formatVersion;
	}
}