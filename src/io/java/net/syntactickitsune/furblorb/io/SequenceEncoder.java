package net.syntactickitsune.furblorb.io;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * The sequence-based specialization of {@link Encoder} (also known as a "keyless" encoder).
 * Its decoding counterpart is {@linkplain SequenceDecoder here}.
 * </p>
 * <p>
 * Whereas the {@code Encoder} interface is designed to work with just about any file format by assuming a structured data format,
 * the {@code SequenceEncoder} interface is specifically designed for formats where only order matters.
 * In addition to key-based methods, this interface also provides keyless methods for writing directly to a sequence of data.
 * </p>
 * <p>
 * {@code SequenceEncoder} implements the {@code Encoder} interface, and redirects all of the methods to their keyless counterparts.
 * It does not check the keys; they may be {@code null}.
 * </p>
 * @author SyntacticKitsune
 * @see SequenceDecoder
 */
public interface SequenceEncoder extends Encoder {

	/**
	 * Writes the given {@code byte} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeByte(byte value);

	/**
	 * Writes all of the {@code byte}s in the given {@code byte} array to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 */
	public void writeBytes(byte[] value);

	/**
	 * Writes the given (optional) {@code byte} array to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write. May be {@code null}.
	 */
	public void writeByteArray(byte @Nullable [] value);

	/**
	 * Writes the given {@code boolean} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeBoolean(boolean value);

	/**
	 * Writes the given {@code char} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeChar(char value);

	/**
	 * Writes the given {@code short} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeShort(short value);

	/**
	 * Writes the given {@code int} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeInt(int value);

	/**
	 * <p>
	 * Writes the next <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint">seven-bit {@code int}</a>
	 * to this {@code SequenceEncoder}'s sequence.
	 * </p>
	 * <p>
	 * A seven-bit {@code int} is a special-encoded {@code int} where the most-significant bit determines whether to read more {@code int}.
	 * The idea is that for small {@code int}s only one {@code byte} needs to be used, which is ideal for lists and strings.
	 * The downside to this approach is that the worst-case means that <i>five</i> {@code byte}s are used,
	 * and negative {@code int}s always encounter this because the most-significant bit describes the sign.
	 * </p>
	 * @param value The value to write.
	 */
	public void write7BitInt(int value);

	/**
	 * Writes the given {@code long} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeLong(long value);

	/**
	 * Writes the given {@code float} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeFloat(float value);

	/**
	 * Writes the given {@code double} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 */
	public void writeDouble(double value);

	/**
	 * Writes the given {@link UUID} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 */
	public void writeUUID(UUID value);

	/**
	 * Writes the given {@link String} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @see #writeFixedLengthString(String)
	 */
	public void writeString(String value);

	/**
	 * Writes the characters from the given {@link String} to this {@code SequenceEncoder}'s sequence.
	 * Unlike {@link #writeString(String)}, the length of the string will <i>not</i> be written.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @see #writeString(String)
	 */
	public void writeFixedLengthString(String value);

	/**
	 * Writes the given {@code enum} constant to this {@code SequenceEncoder}'s sequence.
	 * The constant will be encoded using its ordinal.
	 * @param <E> The {@code enum} type.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 */
	public <E extends Enum<E> & INamedEnum> void writeEnum(E value);

	/**
	 * Writes the given {@link List} of {@link Object Objects} to this {@code SequenceEncoder}'s sequence.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 */
	public <T> void writeObjectList(Collection<T> value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@link List} of {@link Object Objects} to this {@code SequenceEncoder}'s sequence.
	 * The {@code List} may contain {@code null} values.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 */
	public <T> void writeOptionalObjectList(Collection<@Nullable T> value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@code Object} to this {@code SequenceEncoder}'s sequence using the provided writer.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the value.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 */
	public <T> void writeObject(T value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@code Object} -- which may be {@code null} -- to this {@code SequenceEncoder}'s sequence using the provided writer.
	 * A {@code null} value will <i>not</i> be passed to the writer.
	 * @param value The value to write. May be {@code null}.
	 * @param writer A {@link BiConsumer} to handle writing the (non-{@code null}) value.
	 * @throws NullPointerException If {@code writer} is {@code null}.
	 */
	public <T> void writeOptionalObject(@Nullable T value, BiConsumer<T, Encoder> writer);

	// ===== OVERRIDES =====

	@Override
	public default void writeByte(@Nullable String key, byte value) { writeByte(value); }

	@Override
	public default void writeByteArray(@Nullable String key, byte @Nullable [] value) { writeByteArray(value); }

	@Override
	public default void writeBoolean(@Nullable String key, boolean value) { writeBoolean(value); }

	@Override
	public default void writeShort(@Nullable String key, short value) { writeShort(value); }

	@Override
	public default void writeInt(@Nullable String key, int value) { writeInt(value); }

	@Override
	public default void writeLong(@Nullable String key, long value) { writeLong(value); }

	@Override
	public default void writeFloat(@Nullable String key, float value) { writeFloat(value); }

	@Override
	public default void writeDouble(@Nullable String key, double value) { writeDouble(value); }

	@Override
	public default void writeUUID(@Nullable String key, UUID value) { writeUUID(value); }

	@Override
	public default void writeString(@Nullable String key, String value) { writeString(value); }

	@Override
	public default <E extends Enum<E> & INamedEnum> void writeEnum(@Nullable String key, E value) { writeEnum(value); }

	@Override
	public default <T> void writeObjectList(@Nullable String key, Collection<T> value, BiConsumer<T, Encoder> writer) { writeObjectList(value, writer); }

	@Override
	public default <T> void writeOptionalObjectList(@Nullable String key, Collection<@Nullable T> value, BiConsumer<T, Encoder> writer) { writeOptionalObjectList(value, writer); }

	@Override
	public default void writeStringList(@Nullable String key, List<String> value) { writeList(value, (v, enc) -> enc.writeString(null, v)); }

	@Override
	public default <T> void writeObject(@Nullable String key, T value, BiConsumer<T, Encoder> writer) { writer.accept(value, this); }

	@Override
	public default <T> void writeOptionalObject(@Nullable String key, @Nullable T value, BiConsumer<T, Encoder> writer) { writeOptionalObject(value, writer); }

	@Override
	public default <T> void writeExternal(@Nullable String key, T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) { writer.accept(key, value, this); }

	@Override
	public default <T> void writeExternalOptional(@Nullable String key, @Nullable T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) { writeOptionalObject(value, (val, enc) -> writer.accept(key, val, enc)); }
}