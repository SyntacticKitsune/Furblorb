package net.syntactickitsune.furblorb.api.io;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * Handles reading from and writing to external files.
 * @author SyntacticKitsune
 * @see Encoder#writeExternal(String, Object, TriConsumer, Function)
 * @see Decoder#readExternal(String, BiFunction, Function)
 */
public interface ExternalFileHandler {

	/**
	 * Optionally used by {@code Encoder}/{@code Decoder}s to decide whether to write/read external files.
	 * @param filename The name of the file.
	 * @return {@code true} if the handler will handle this file.
	 * @throws NullPointerException If {@code filename} is {@code null}.
	 */
	public default boolean handles(String filename) {
		Objects.requireNonNull(filename, "filename");
		return true;
	}

	/**
	 * Writes the given bytes to the external file with the given name.
	 * @param filename The name of the file.
	 * @param contents The new contents of the file.
	 * @throws NullPointerException If {@code filename} or {@code contents} are {@code null}.
	 * @throws UnsupportedOperationException If the {@code ExternalFileHandler} does not support writing files (like if it's read-only).
	 */
	public default void writeExternalFile(String filename, byte[] contents) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Reads and returns the contents of the external file with the given name.
	 * If the requested external file doesn't exist, {@code null} is returned.
	 * @param filename The name of the file.
	 * @return The contents of the file, or {@code null} if the file doesn't exist.
	 * @throws NullPointerException If {@code filename} is {@code null}.
	 * @throws UnsupportedOperationException If the {@code ExternalFileHandler} does not support reading files (like if it's write-only).
	 */
	public default byte @Nullable [] readExternalFile(String filename) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Normalizes the line endings of the specified {@code String}.
	 * @param in The input string.
	 * @return The input string, with normalized line endings.
	 */
	public default String normalizeLineEndings(String in) {
		return in.replace("\r\n", "\n");
	}
}