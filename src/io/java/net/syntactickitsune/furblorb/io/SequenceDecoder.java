package net.syntactickitsune.furblorb.io;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * The sequence-based specialization of {@link Decoder} (also known as a "keyless" decoder).
 * Its encoding counterpart is {@linkplain SequenceEncoder here}.
 * </p>
 * <p>
 * Whereas the {@code Decoder} interface is designed to work with just about any file format by assuming a structured data format,
 * the {@code SequenceDecoder} interface is specifically designed for formats where only order matters.
 * In addition to key-based methods, this interface also provides keyless methods for reading directly from a sequence of data.
 * </p>
 * <p>
 * {@code SequenceDecoder} implements the {@code Decoder} interface, and redirects all of the methods to their keyless counterparts.
 * It does not check the keys; they may be {@code null}.
 * </p>
 * @author SyntacticKitsune
 * @see SequenceEncoder
 */
public interface SequenceDecoder extends Decoder {

	/**
	 * <p>Returns whether or not this {@code SequenceDecoder}'s sequence has any remaining data in it.</p>
	 * <p>This method can be used to ensure there is no trailing data, which can be useful for debugging purposes.</p>
	 * @return {@code true} if there is any remaining data.
	 */
	public boolean hasRemaining();

	/**
	 * Reads the next {@code byte} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public byte readByte();

	/**
	 * Reads the next {@code len} {@code byte}s from this {@code SequenceDecoder}'s sequence.
	 * @param len The number of {@code byte}s to read.
	 * @return The read values, as a {@code len}-sized {@code byte} array.
	 */
	public default byte[] readBytes(int len) {
		return readBytes(new byte[len]);
	}

	/**
	 * Reads the next {@code array.length} {@code byte}s from this {@code SequenceDecoder}'s sequence -- that is, enough to fill the provided array.
	 * @param array The array to fill.
	 * @return The given array.
	 * @throws NullPointerException If {@code array} is {@code null}.
	 */
	public byte[] readBytes(byte[] array);

	/**
	 * Reads the next (optional) {@code byte} array from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public byte @Nullable [] readByteArray();

	/**
	 * Reads the next {@code boolean} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public boolean readBoolean();

	/**
	 * Reads the next {@code char} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public char readChar();

	/**
	 * Reads the next {@code short} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public short readShort();

	/**
	 * Reads the next {@code int} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public int readInt();

	/**
	 * <p>
	 * Reads the next <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint">seven-bit {@code int}</a>
	 * from this {@code SequenceDecoder}'s sequence.
	 * </p>
	 * <p>
	 * A seven-bit {@code int} is a special-encoded {@code int} where the most-significant bit determines whether to read more {@code int}.
	 * The idea is that for small {@code int}s only one {@code byte} needs to be used, which is ideal for lists and strings.
	 * The downside to this approach is that the worst-case means that <i>five</i> {@code byte}s are used,
	 * and negative {@code int}s always encounter this because the most-significant bit describes the sign.
	 * </p>
	 * @return The read value.
	 * @throws FurblorbParsingException If the {@code int} described is too long.
	 */
	public int read7BitInt();

	/**
	 * Reads the next {@code long} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public long readLong();

	/**
	 * Reads the next {@code float} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public float readFloat();

	/**
	 * Reads the next {@code double} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public double readDouble();

	/**
	 * Reads the next C#-style {@link UUID} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 */
	public UUID readUUID();

	/**
	 * Reads the next {@link String} from this {@code SequenceDecoder}'s sequence.
	 * @return The read value.
	 * @see #readFixedLengthString(int)
	 */
	public String readString();

	/**
	 * Reads the next {@code length} characters from this {@code SequenceDecoder}'s sequence and returns a {@link String} composed from them.
	 * This differs from {@link #readString()} in that it does not read the length of the string from the sequence.
	 * @param length The number of characters to read.
	 * @return The read value.
	 * @throws IllegalArgumentException If {@code length} is negative.
	 * @see #readString()
	 */
	public String readFixedLengthString(int length);

	/**
	 * Reads the next {@code enum} constant from this {@code SequenceDecoder}'s sequence.
	 * The constant will be decoded using an ordinal.
	 * @param <E> The {@code enum} type.
	 * @param type The type of {@code enum} to read. Required to interpret {@code enum} constants correctly.
	 * @return The read value.
	 * @throws NullPointerException If {@code type} is {@code null}.
	 * @throws FurblorbParsingException If the read ordinal is out-of-bounds.
	 */
	public <E extends Enum<E> & INamedEnum> E readEnum(Class<E> type);

	/**
	 * Reads the next {@link List} from this {@code SequenceDecoder}'s sequence.
	 * @param reader A {@code Function} to read the individual values from this {@code SequenceDecoder}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 * @throws FurblorbParsingException If an attempt to read more than 1000 entries is made. This frequently indicates a deserialization bug.
	 */
	public <T> List<T> readObjectList(Function<? super SequenceDecoder, T> reader);

	/**
	 * Reads the next {@link List} from this {@code SequenceDecoder}'s sequence.
	 * The {@code List} may contain {@code null} values.
	 * @param reader A {@code Function} to read the individual values from this {@code SequenceDecoder}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 */
	public <T> List<@Nullable T> readOptionalObjectList(Function<? super SequenceDecoder, T> reader);

	/**
	 * Reads the next {@code Object} from this {@code SequenceDecoder}'s sequence using the provided reader.
	 * @param <T> The kind of {@code Object} to read.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 */
	public <T> T readObject(Function<? super SequenceDecoder, T> reader);

	/**
	 * Reads the next {@code Object} from this {@code SequenceDecoder}'s sequence using the provided reader, if one exists.
	 * Otherwise, returns {@code null}.
	 * @param <T> The kind of {@code Object} to read.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value. May be {@code null}.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 */
	public <T> @Nullable T readOptionalObject(Function<? super SequenceDecoder, T> reader);

	// ===== OVERRIDES =====

	@Override
	public default byte readByte(@Nullable String key) { return readByte(); }

	@Override
	public default byte[] readByteArray(@Nullable String key) { return readByteArray(); }

	@Override
	public default boolean readBoolean(@Nullable String key) { return readBoolean(); }

	@Override
	public default short readShort(@Nullable String key) { return readShort(); }

	@Override
	public default int readInt(@Nullable String key) { return readInt(); }

	@Override
	public default long readLong(@Nullable String key) { return readLong(); }

	@Override
	public default float readFloat(@Nullable String key) { return readFloat(); }

	@Override
	public default double readDouble(@Nullable String key) { return readDouble(); }

	@Override
	public default UUID readUUID(@Nullable String key) { return readUUID(); }

	@Override
	public default String readString(@Nullable String key) { return readString(); }

	@Override
	public default <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type) { return readEnum(type); }

	@Override
	public default <T> List<T> readObjectList(@Nullable String key, Function<Decoder, T> reader) { return readObjectList(reader); }

	@Override
	public default <T> List<@Nullable T> readOptionalObjectList(@Nullable String key, Function<Decoder, T> reader) { return readOptionalObjectList(reader); }

	@Override
	public default List<String> readStringList(@Nullable String key) { return readList(dec -> dec.readString(null)); }

	@Override
	public default <T> T readObject(@Nullable String key, Function<Decoder, T> reader) { return readObject(reader); }

	@Override
	public default <T> @Nullable T readOptionalObject(@Nullable String key, Function<Decoder, T> reader) { return readOptionalObject(reader); }

	@Override
	public default <T> T readExternal(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) { return readObject(dec -> reader.apply(dec, key)); }

	@Override
	@Nullable
	public default <T> T readExternalOptional(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) { return readOptionalObject(dec -> reader.apply(dec, key)); }
}