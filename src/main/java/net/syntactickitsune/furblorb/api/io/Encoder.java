package net.syntactickitsune.furblorb.api.io;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.FurblorbException;
import net.syntactickitsune.furblorb.api.util.TriConsumer;

/**
 * <p>
 * Represents a generalized {@code Map}-style encoder.
 * Its decoding counterpart is {@linkplain Decoder here}.
 * </p>
 * <p>
 * The {@code Encoder} interface is designed to be mostly file-format-agnostic,
 * allowing data to be encoded to {@code JSON}, {@code NBT}, unmaintainable binary files, etc.
 * Methods are provided for writing a variety of different data types, all preferably associated with keys (as generally required by most file formats).
 * </p>
 * @author SyntacticKitsune
 * @see Decoder
 */
public interface Encoder {

	/**
	 * Used by {@link net.syntactickitsune.furblorb.api.finmer.io.FurballSerializables} to decide whether to write ids versus type names.
	 * @return {@code true} for ids, {@code false} for type names.
	 */
	public boolean writeCompressedTypes();

	/**
	 * @return The format version.
	 * @throws FurblorbException If no format version has been set.
	 */
	public byte formatVersion();

	/**
	 * A method called to decide whether serialization code should perform sanity checks on their content.
	 * For example, {@link net.syntactickitsune.furblorb.api.finmer.asset.SceneAsset SceneAssets} enforcing that certain fields <i>don't</i> exist without their associated flag.
	 * @return Whether serialization code should perform checks on the current state.
	 */
	public boolean validate();

	/**
	 * Writes the given {@code byte}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeByte(@Nullable String key, byte value);

	/**
	 * Writes the given (optional) {@code byte} array.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write. May be {@code null}.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeByteArray(@Nullable String key, byte @Nullable [] value);

	/**
	 * Writes the given {@code boolean}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeBoolean(@Nullable String key, boolean value);

	/**
	 * Writes the given {@code short}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeShort(@Nullable String key, short value);

	/**
	 * Writes the given {@code int}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeInt(@Nullable String key, int value);

	/**
	 * Writes the given {@code long}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeLong(@Nullable String key, long value);

	/**
	 * Writes the given {@code float}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeFloat(@Nullable String key, float value);

	/**
	 * Writes the given {@code double}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeDouble(@Nullable String key, double value);

	/**
	 * Writes the given {@link UUID}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null} or {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeUUID(@Nullable String key, UUID value);

	/**
	 * Writes the given {@link String}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null} or {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeString(@Nullable String key, String value);

	/**
	 * Writes the given {@code enum} constant.
	 * The constant may be encoded using its ordinal or with its {@linkplain INamedEnum name}.
	 * @param <E> The {@code enum} type.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null} or {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public <E extends Enum<E> & INamedEnum> void writeEnum(@Nullable String key, E value);

	/**
	 * Writes the given {@link List}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}, or if {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public <T> void writeList(@Nullable String key, List<T> value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@link List}, which may contain {@code null} values.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}, or if {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public <T> void writeOptionalList(@Nullable String key, List<@Nullable T> value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@link String} {@link List}.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null} or if {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public void writeStringList(@Nullable String key, List<String> value);

	/**
	 * Writes the given {@code Object} using the provided writer.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the value.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}, or if {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public <T> void write(@Nullable String key, T value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given {@code Object} -- which may be {@code null} -- using the provided writer.
	 * A {@code null} value will <i>not</i> be passed to the writer.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support keys.
	 * @param value The value to write. May be {@code null}.
	 * @param writer A {@link BiConsumer} to handle writing the (non-{@code null}) value.
	 * @throws NullPointerException If {@code writer} is {@code null} or if {@code key} is {@code null} and this {@code Encoder} requires keys.
	 */
	public <T> void writeOptional(@Nullable String key, @Nullable T value, BiConsumer<T, Encoder> writer);

	/**
	 * Writes the given "external" value -- that is, a value from some other file.
	 * It is up to the {@code Encoder} to decide whether it wants to write this value to itself or to another file.
	 * The two {@code writer} parameters handle transforming the value into something the {@code Encoder} understands.
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support external files nor keys.
	 * @param value The value to write.
	 * @param writer A {@link TriConsumer} to handle writing the value inline.
	 * @param externalWriter A {@link Function} to handle writing the value to an external file.
	 * @throws NullPointerException If {@code value}, {@code writer}, or {@code externalWriter} are {@code null}, or if {@code key} is {@code null} and this {@code Encoder} requires keys or attempts to write the value to an external file.
	 */
	public <T> void writeExternal(@Nullable String key, T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter);

	/**
	 * Writes the given "external" value -- that is, a value from some other file. It may be {@code null}.
	 * It is up to the {@code Encoder} to decide whether it wants to write this value to itself or to another file.
	 * The two {@code writer} parameters handle transforming the value into something the {@code Encoder} understands (if non-{@code null}).
	 * @param key The key to associate the value with. May be {@code null} if the {@code Encoder} doesn't support external files nor keys.
	 * @param value The value to write. May be {@code null}.
	 * @param writer A {@link TriConsumer} to handle writing the value inline.
	 * @param externalWriter A {@link Function} to handle writing the value to an external file.
	 * @throws NullPointerException If {@code value}, {@code writer}, or {@code externalWriter} are {@code null}, or if {@code key} is {@code null} and this {@code Encoder} requires keys or attempts to write the value to an external file.
	 */
	public <T> void writeExternalOptional(@Nullable String key, @Nullable T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter);

	/**
	 * <p>
	 * Throws an exception if {@code value} is <i>not</i> {@code null}.
	 * For {@code Encoders} with {@link #validate()} set to {@code false}, this method may do nothing.
	 * </p>
	 * <p>
	 * This method is intended for pointing out subtle errors in structured data that might otherwise go unnoticed,
	 * such as using a field in an unsupported context.
	 * </p>
	 * @param key The key of the data which should not be present.
	 * @param value The value that should not exist.
	 * @param message Some informative string to tack onto the thrown exception.
	 * @throws FurblorbParsingException If the specified value is {@code null}.
	 * @throws NullPointerException If either {@code key} or {@code message} are {@code null}.
	 */
	public default void assertDoesNotExist(String key, Object value, String message) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(message, "message");
		if (validate() && value != null)
			throw new FurblorbParsingException("Assertion \"" + key + " = null\" failed: " + message);
	}
}