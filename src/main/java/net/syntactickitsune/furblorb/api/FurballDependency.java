package net.syntactickitsune.furblorb.api;

import java.util.Objects;
import java.util.UUID;

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
}