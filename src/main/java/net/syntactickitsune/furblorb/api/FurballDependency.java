package net.syntactickitsune.furblorb.api;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents a dependency on a furball.
 * @param id The unique identifier of the furball being depended on.
 * @param filename The name of the furball file.
 */
public record FurballDependency(UUID id, String filename) {

	/**
	 * Constructs a new {@code FurballDependency}.
	 * @param id The unique identifier of the furball being depended on.
	 * @param filename The name of the furball file.
	 * @throws NullPointerException If {@code id} or {@code filename} are {@code null}.
	 */
	public FurballDependency {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(filename, "filename");
	}

	/**
	 * Decodes a {@code FurballDependency} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public FurballDependency(Decoder in) {
		this(in.readUUID("ID"), in.readString("FileNameHint"));
	}

	/**
	 * Writes this {@code FurballDependency} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeUUID("ID", id);
		to.writeString("FileNameHint", filename);
	}
}