package net.syntactickitsune.furblorb.io.codec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.ExternalFileHandler;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;
import net.syntactickitsune.furblorb.io.TriConsumer;

/**
 * <p>
 * {@code JsonCodec} is an {@link Encoder}/{@link Decoder} hybrid that works with json data.
 * Internally it uses {@link JsonObject} instances.
 * {@link JsonArrayCodec} is its sequence-based counterpart.
 * </p>
 * <p>
 * While {@code JsonCodec} is primarily designed with the purpose of reading/writing Finmer projects in mind, it can also
 * be used as a general purpose wrapper around Gson's json trees.
 * </p>
 * @author SyntacticKitsune
 * @see BinaryCodec
 * @see JsonArrayCodec
 */
public class JsonCodec extends Codec {

	/**
	 * The wrapped {@link JsonObject}.
	 */
	protected final JsonObject wrapped;

	/**
	 * The {@link ExternalFileHandler}. In many cases this will be an {@link net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ExtendedExternalFileHandler ExtendedExternalFileHandler}.
	 */
	@Nullable
	protected final ExternalFileHandler externalFiles;

	/**
	 * Constructs a new {@code JsonCodec} with the specified parameters.
	 * @param root The root object.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to read and write them inline.
	 * @param mode The mode that the {@code JsonCodec} should be in.
	 * @throws NullPointerException If {@code root} or {@code mode} are {@code null}.
	 */
	public JsonCodec(JsonObject root, @Nullable ExternalFileHandler externalFiles, CodecMode mode) {
		super(mode);
		wrapped = Objects.requireNonNull(root, "root");
		this.externalFiles = externalFiles;
	}

	/**
	 * Constructs a new {@code JsonCodec} with the specified parameters.
	 * @param root The root object.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to read and write them inline.
	 * @param mode The mode that the {@code JsonCodec} should be in.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 * @throws NullPointerException If {@code root} or {@code mode} are {@code null}.
	 */
	public JsonCodec(JsonObject root, @Nullable ExternalFileHandler externalFiles, CodecMode mode, byte formatVersion) {
		this(root, externalFiles, mode);
		this.formatVersion = formatVersion;
	}

	/**
	 * Constructs a new write-only {@code JsonCodec} with the specified parameters.
	 * The backing {@code JsonObject} can be accessed via {@link #unwrap()}.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to write them inline.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 */
	public JsonCodec(@Nullable ExternalFileHandler externalFiles, byte formatVersion) {
		this(new JsonObject(), externalFiles, CodecMode.WRITE_ONLY, formatVersion);
	}

	/**
	 * Returns the {@code JsonCodec}'s backing {@link JsonObject}.
	 * @return The backing {@link JsonObject}.
	 */
	public JsonObject unwrap() {
		return wrapped;
	}

	@Override
	public boolean readCompressedTypes() {
		return false;
	}

	@Override
	public boolean writeCompressedTypes() {
		return false;
	}

	@Override
	public byte readByte(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsByte();
	}

	@Override
	public byte[] readByteArray(@Nullable String key) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(Objects.requireNonNull(key, "key"));
		final byte[] ret = new byte[arr.size()];

		for (int i = 0; i < arr.size(); i++)
			ret[i] = arr.get(i).getAsByte();

		return ret;
	}

	@Override
	public byte @Nullable [] readOptionalByteArray(@Nullable String key) {
		checkRead();
		if (!wrapped.has(Objects.requireNonNull(key, "key"))) return null;
		return readByteArray(key);
	}

	@Override
	public boolean readBoolean(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsBoolean();
	}

	@Override
	public short readShort(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsShort();
	}

	@Override
	public int readInt(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsInt();
	}

	@Override
	public long readLong(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsLong();
	}

	@Override
	public float readFloat(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsFloat();
	}

	@Override
	public double readDouble(@Nullable String key) {
		checkRead();
		return wrapped.get(Objects.requireNonNull(key, "key")).getAsDouble();
	}

	@Override
	public UUID readUUID(@Nullable String key) {
		checkRead();
		return UUID.fromString(readString(Objects.requireNonNull(key, "key")));
	}

	@Override
	public String readString(@Nullable String key) {
		checkRead();
		final JsonElement elem = wrapped.get(Objects.requireNonNull(key, "key"));
		return elem == null ? "" : elem.getAsString(); // Coerce to "".
	}

	@Override
	public <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type) {
		checkRead();
		return getConstantById(readString(key), type);
	}

	static <E extends Enum<E> & INamedEnum> E getConstantById(String id, Class<E> type) {
		final E[] constants = type.getEnumConstants();
		for (E e : constants)
			if (id.equals(e.id()))
				return e;

		throw new FurblorbParsingException("No " + type.getName() + " with id '" + id + "'");
	}

	@Override
	public <T> List<T> readObjectList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return readListOf(key, dec -> dec.readObject(reader));
	}

	@Override
	public <T> List<@Nullable T> readOptionalObjectList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return readListOf(key, dec -> dec.readOptionalObject(reader));
	}

	@Override
	public <T> List<T> readListOf(@Nullable String key, Function<SequenceDecoder, T> reader) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(Objects.requireNonNull(key, "key"));
		final JsonArrayCodec codec = new JsonArrayCodec(arr, externalFiles, CodecMode.READ_ONLY, formatVersion);
		final List<T> ret = new ArrayList<>(arr.size());

		for (int i = 0; i < arr.size(); i++)
			ret.add(reader.apply(codec));

		return ret;
	}

	@Override
	public <T> T readObject(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return reader.apply(new JsonCodec(wrapped.getAsJsonObject(key), externalFiles, mode, formatVersion));
	}

	@Override
	@Nullable
	public <T> T readOptionalObject(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return wrapped.has(key) ? readObject(key, reader) : null;
	}

	@Override
	public <T> T readExternal(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) {
		checkRead();
		if (externalFiles == null || !externalFiles.handles(key))
			return reader.apply(this, key);

		final byte[] contents = externalFiles.readExternalFile(key);

		return externalReader.apply(contents);
	}

	@Override
	@Nullable
	public <T> T readExternalOptional(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) {
		checkRead();
		if (externalFiles == null || !externalFiles.handles(key))
			return wrapped.has(key) ? reader.apply(this, key) : null;

		final byte[] contents = externalFiles.readExternalFile(key);

		return contents == null ? null : externalReader.apply(contents);
	}

	@Override
	public void writeByte(@Nullable String key, byte value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeByteArray(@Nullable String key, byte[] value) {
		checkWrite();
		Objects.requireNonNull(key, "key");

		final JsonArray arr = new JsonArray(value.length);
		for (byte v : value) arr.add(v);
		wrapped.add(key, arr);
	}

	@Override
	public void writeOptionalByteArray(@Nullable String key, byte @Nullable [] value) {
		checkWrite();
		if (value != null)
			writeByteArray(key, value);
	}

	@Override
	public void writeBoolean(@Nullable String key, boolean value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeShort(@Nullable String key, short value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeInt(@Nullable String key, int value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeLong(@Nullable String key, long value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeFloat(@Nullable String key, float value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeDouble(@Nullable String key, double value) {
		checkWrite();
		wrapped.addProperty(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeUUID(@Nullable String key, UUID value) {
		checkWrite();
		writeString(Objects.requireNonNull(key, "key"), value.toString());
	}

	@Override
	public void writeString(@Nullable String key, String value) {
		checkWrite();
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		if (!value.isEmpty())
			wrapped.addProperty(key, value);
	}

	@Override
	public <E extends Enum<E> & INamedEnum> void writeEnum(@Nullable String key, E value) {
		checkWrite();
		writeString(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value.id(), value.getClass().getSimpleName() + " violated INamedEnum contract"));
	}

	@Override
	public <T> void writeObjectList(@Nullable String key, Collection<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		writeListOf(key, value, (enc, v) -> enc.writeObject(v, writer));
	}

	@Override
	public <T> void writeOptionalObjectList(@Nullable String key, Collection<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		writeListOf(key, value, (enc, v) -> enc.writeOptionalObject(v, writer));
	}

	@Override
	public <T> void writeListOf(@Nullable String key, Collection<T> value, BiConsumer<SequenceEncoder, T> writer) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.size());
		final JsonArrayCodec codec = new JsonArrayCodec(arr, externalFiles, CodecMode.WRITE_ONLY, formatVersion);

		for (T v : value)
			writer.accept(codec, Objects.requireNonNull(v));

		wrapped.add(key, arr);
	}

	@Override
	public <T> void writeObject(@Nullable String key, T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final JsonCodec codec = new JsonCodec(externalFiles, formatVersion);
		writer.accept(value, codec);
		wrapped.add(key, codec.wrapped);
	}

	@Override
	public <T> void writeOptionalObject(@Nullable String key, @Nullable T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		if (value != null)
			writeObject(key, value, writer);
	}

	@Override
	public <T> void writeExternal(@Nullable String key, T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) {
		checkWrite();
		if (externalFiles == null || !externalFiles.handles(key)) {
			writer.accept(key, value, this);
			return;
		}

		final byte[] b = externalWriter.apply(value);
		if (b == null) return; // Just so I don't have to go perform some annoying refactors.

		externalFiles.writeExternalFile(key, b);
	}

	@Override
	public <T> void writeExternalOptional(@Nullable String key, @Nullable T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) {
		checkWrite();
		if (value == null) return;

		writeExternal(key, value, writer, externalWriter);
	}

	@Override
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException {
		checkRead();
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(message, "message");
		if (validate() && wrapped.has(key))
			throw new FurblorbParsingException("Assertion \"" + key + " = null\" failed: " + message);
	}

	/**
	 * Checks to see make sure read access is supported.
	 * @throws UnsupportedOperationException If the codec is write-only.
	 */
	protected void checkRead() {
		if (!mode.canRead()) throw new UnsupportedOperationException("Codec is write-only");
	}

	/**
	 * Checks to see make sure write access is supported.
	 * @throws UnsupportedOperationException If the codec is read-only.
	 */
	protected void checkWrite() {
		if (!mode.canWrite()) throw new UnsupportedOperationException("Codec is read-only");
	}
}