package net.syntactickitsune.furblorb.io;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Represents a generalized {@code Map}-style decoder.
 * Its encoding counterpart is {@linkplain Encoder here}.
 * </p>
 * <p>
 * The {@code Decoder} interface is designed to be mostly file-format-agnostic,
 * allowing data to be decoded from {@code JSON}, {@code NBT}, unmaintainable binary files, etc.
 * Methods are provided for reading a variety of different data types, all preferably associated with keys (as generally required by most file formats).
 * </p>
 * @author SyntacticKitsune
 * @see Encoder
 */
public interface Decoder {

	/**
	 * Used by {@link net.syntactickitsune.furblorb.finmer.io.FurballSerializables} to decide whether to read ids versus type names.
	 * @return {@code true} for ids, {@code false} for type names.
	 */
	public boolean readCompressedTypes();

	/**
	 * @return The format version.
	 */
	public byte formatVersion();

	/**
	 * A method called to decide whether serialization code should perform sanity checks on their content.
	 * For example, {@link net.syntactickitsune.furblorb.finmer.asset.SceneAsset SceneAssets} enforcing that certain fields <i>don't</i> exist without their associated flag.
	 * @return Whether serialization code should perform checks on the current state.
	 */
	public boolean validate();

	/**
	 * Reads the next {@code byte} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public byte readByte(@Nullable String key);

	/**
	 * Reads the next {@code byte} array from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public byte[] readByteArray(@Nullable String key);

	/**
	 * Reads the next (optional) {@code byte} array from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value. May be {@code null}.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public byte @Nullable [] readOptionalByteArray(@Nullable String key);

	/**
	 * Reads the next {@code boolean} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public boolean readBoolean(@Nullable String key);

	/**
	 * Reads the next {@code short} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public short readShort(@Nullable String key);

	/**
	 * Reads the next {@code int} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public int readInt(@Nullable String key);

	/**
	 * <p>
	 * Reads the next compressed {@code int} from this {@code Decoder}'s sequence.
	 * </p>
	 * <p>
	 * This method is the counterpart to {@link Encoder#writeCompressedInt(String, int)},
	 * and intended for reading {@code int}s that were written using that method.
	 * </p>
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public default int readCompressedInt(@Nullable String key) {
		return readInt(key);
	}

	/**
	 * Reads the next {@code long} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public long readLong(@Nullable String key);

	/**
	 * Reads the next {@code float} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public float readFloat(@Nullable String key);

	/**
	 * Reads the next {@code double} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public double readDouble(@Nullable String key);

	/**
	 * Reads the next {@link UUID} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public UUID readUUID(@Nullable String key);

	/**
	 * Reads the next {@link String} from this {@code Decoder}'s sequence.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read value.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public String readString(@Nullable String key);

	/**
	 * Reads the next {@code enum} constant from this {@code Decoder}'s sequence.
	 * The constant may be decoded using an ordinal or with a {@linkplain INamedEnum name}.
	 * @param <E> The {@code enum} type.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param type The type of {@code enum} to read. Required to interpret {@code enum} constants correctly.
	 * @return The read value.
	 * @throws NullPointerException If {@code type} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public default <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type) {
		return readEnum(key, type, INamedEnum::id);
	}

	/**
	 * Reads the next {@code enum} constant from this {@code Decoder}'s sequence.
	 * The constant may be decoded using an ordinal or with a {@linkplain INamedEnum name}.
	 * @param <E> The {@code enum} type.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param type The type of {@code enum} to read. Required to interpret {@code enum} constants correctly.
	 * @param idFunction A {@link Function} to get the id of a given enum constant. Useful for format-version-specific ids.
	 * @return The read value.
	 * @throws NullPointerException If {@code type} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type, Function<E, String> idFunction);

	/**
	 * Reads the next {@link List} of {@link Object Objects} from this {@code Decoder}'s sequence.
	 * @param key The key that the list is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param reader A {@code Function} to read the individual values from this {@code Decoder}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public <T> List<T> readObjectList(@Nullable String key, Function<Decoder, T> reader);

	/**
	 * Reads the next {@link List} of {@link Object Objects} from this {@code Decoder}'s sequence.
	 * The {@code List} may contain {@code null} values.
	 * @param key The key that the list is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param reader A {@code Function} to read the individual values from this {@code Decoder}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public <T> List<@Nullable T> readOptionalObjectList(@Nullable String key, Function<Decoder, T> reader);

	/**
	 * Reads the contents of the next {@link List} from this {@code Decoder}'s sequence using the provided reader.
	 * @param <T> The return value of the function.
	 * @param key The key that the list is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param reader A {@code Function} to read the values of the list.
	 * @return The read values.
	 * @throws NullPointerException If {@code reader} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public <T> List<T> readListOf(@Nullable String key, Function<SequenceDecoder, T> reader);

	/**
	 * Reads the next {@link String} {@link List} from this {@code Decoder}'s sequence.
	 * @param key The key that the list is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @return The read list.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Decoder} requires keys.
	 */
	public default List<String> readStringList(@Nullable String key) {
		return readListOf(key, SequenceDecoder::readString);
	}

	/**
	 * Reads the next {@code Object} from this {@code Decoder}'s sequence using the provided reader.
	 * @param <T> The kind of {@code Object} to read.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value.
	 * @throws NullPointerException If {@code reader} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	public <T> T readObject(@Nullable String key, Function<Decoder, T> reader);

	/**
	 * Reads the next {@code Object} from this {@code Decoder}'s sequence using the provided reader, if one exists.
	 * Otherwise, returns {@code null}.
	 * @param <T> The kind of {@code Object} to read.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support keys.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value. May be {@code null}.
	 * @throws NullPointerException If {@code reader} is {@code null} or {@code key} is {@code null} and this {@code Decoder} requires keys.
	 * @since 2.0.0
	 */
	@Nullable
	public <T> T readOptionalObject(@Nullable String key, Function<Decoder, T> reader);

	/**
	 * Reads an "external" value from this {@code Decoder}'s sequence -- that is, a value from some other file.
	 * It is up to the {@code Decoder} to decide whether it wants to read this value from itself or from another file.
	 * The two {@code reader} parameters handle transforming the value into what your code expects.
	 * @param <T> The kind of value to read.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support external files nor keys.
	 * @param reader A {@code BiFunction} to read the desired value from the {@code Decoder}.
	 * @param externalReader A {@code Function} to parse the contents of the external file.
	 * @return The read value.
	 * @throws NullPointerException If {@code reader} or {@code externalReader} is {@code null}, or if {@code key} is {@code null} and this {@code Decoder} requires keys or chooses to read an external file.
	 */
	public <T> T readExternal(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader);

	/**
	 * Reads an "external" value from this {@code Decoder}'s sequence -- that is, a value from some other file.
	 * This value may be {@code null} or otherwise not exist.
	 * It is up to the {@code Decoder} to decide whether it wants to read this value from itself or from another file.
	 * The two {@code reader} parameters handle transforming the value into what your code expects.
	 * @param <T> The kind of value to read.
	 * @param key The key that the value is associated with. May be {@code null} if the {@code Decoder} doesn't support external files nor keys.
	 * @param reader A {@code BiFunction} to read the desired value from the {@code Decoder}.
	 * @param externalReader A {@code Function} to parse the contents of the external file.
	 * @return The read value, or {@code null} if the value is {@code null} or missing.
	 * @throws NullPointerException If {@code reader} or {@code externalReader} is {@code null}, or if {@code key} is {@code null} and this {@code Decoder} requires keys or chooses to read an external file.
	 */
	@Nullable
	public <T> T readExternalOptional(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader);

	/**
	 * <p>
	 * Throws an exception if there is any data associated with {@code key} in this {@code Decoder}.
	 * For keyless {@code Decoders} or {@code Decoders} with {@link #validate()} set to {@code false}, this method may do nothing.
	 * </p>
	 * <p>
	 * This method is intended for pointing out subtle errors in structured data that might otherwise go unnoticed,
	 * such as using a field in an unsupported context.
	 * </p>
	 * @param key The key of the data which should not be present.
	 * @param message Some informative string to tack onto the thrown exception.
	 * @throws FurblorbParsingException If this {@code Decoder} supports keys and there exists data associated with the specified key.
	 * @throws NullPointerException If either {@code key} or {@code message} are {@code null}.
	 */
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException;
}