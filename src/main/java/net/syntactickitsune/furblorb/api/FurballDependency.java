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

	public FurballDependency(Decoder in) {
		this(in.readUUID("ID"), in.readString("FileNameHint"));
	}

	public void write(Encoder to) {
		to.writeUUID("ID", id);
		to.writeString("FileNameHint", filename);
	}
}