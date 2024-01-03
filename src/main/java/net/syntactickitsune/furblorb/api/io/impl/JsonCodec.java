package net.syntactickitsune.furblorb.api.io.impl;

import java.util.ArrayList;
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

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.ExternalFileHandler;
import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.util.TriConsumer;

/**
 * <p>
 * {@code JsonCodec} is an {@link Encoder}/{@link Decoder} hybrid that works with json data.
 * Internally it uses {@link JsonObject} instances.
 * </p>
 * <p>
 * While {@code JsonCodec} is primarily designed with the purpose of reading/writing Finmer projects in mind, it can also
 * be used as a general purpose wrapper around Gson's json trees.
 * </p>
 * @author SyntacticKitsune
 * @see BinaryCodec
 */
public class JsonCodec extends Codec {

	protected final JsonObject wrapped;
	@Nullable
	protected final ExternalFileHandler externalFiles;
	protected final boolean read;

	/**
	 * Constructs a new {@code JsonCodec} with the specified parameters.
	 * @param root The root object.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to write them inline.
	 * @param read Whether the {@code JsonCodec} should be read-only versus write-only.
	 * @throws NullPointerException If {@code root} is {@code null}.
	 */
	public JsonCodec(JsonObject root, @Nullable ExternalFileHandler externalFiles, boolean read) {
		wrapped = Objects.requireNonNull(root, "root");
		this.externalFiles = externalFiles;
		this.read = read;
	}

	/**
	 * Constructs a new {@code JsonCodec} with the specified parameters.
	 * @param root The root object.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to write them inline.
	 * @param read Whether the {@code JsonCodec} should be read-only versus write-only.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 * @throws NullPointerException If {@code root} is {@code null}.
	 */
	public JsonCodec(JsonObject root, @Nullable ExternalFileHandler externalFiles, boolean read, byte formatVersion) {
		this(root, externalFiles, read);
		this.formatVersion = formatVersion;
	}

	/**
	 * Constructs a new write-only {@code JsonCodec} with the specified parameters.
	 * The backing {@code JsonObject} can be accessed via {@link #unwrap()}.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonCodec} to write them inline.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 */
	public JsonCodec(@Nullable ExternalFileHandler externalFiles, byte formatVersion) {
		this(new JsonObject(), externalFiles, false, formatVersion);
	}

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
	@Nullable
	public byte[] readByteArray(@Nullable String key) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(key);
		final byte[] ret = new byte[arr.size()];

		for (int i = 0; i < arr.size(); i++)
			ret[i] = arr.get(i).getAsByte();

		return ret;
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
		final String str = readString(key);

		final E[] constants = type.getEnumConstants();
		for (E e : constants)
			if (str.equals(e.id()))
				return e;

		throw new FurblorbParsingException("No " + type.getName() + " with id '" + str + "'");
	}

	@Override
	public <T> List<T> readList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(key);
		final List<T> ret = new ArrayList<>(arr.size());

		for (int i = 0; i < arr.size(); i++) {
			final JsonObject obj = arr.get(i).getAsJsonObject();
			ret.add(reader.apply(new JsonCodec(obj, externalFiles, read, formatVersion)));
		}

		return ret;
	}

	@Override
	public <T> List<@Nullable T> readOptionalList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(key);
		final List<T> ret = new ArrayList<>(arr.size());

		for (int i = 0; i < arr.size(); i++)
			if (arr.get(i) instanceof JsonObject obj)
				ret.add(reader.apply(new JsonCodec(obj, externalFiles, read, formatVersion)));
			else
				ret.add(null);

		return ret;
	}

	@Override
	public List<String> readStringList(@Nullable String key) {
		checkRead();
		final JsonArray arr = wrapped.getAsJsonArray(key);
		final List<String> ret = new ArrayList<>(arr.size());

		for (int i = 0; i < arr.size(); i++)
			ret.add(arr.get(i).getAsString());

		return ret;
	}

	@Override
	public <T> T read(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return reader.apply(new JsonCodec(wrapped.getAsJsonObject(key), externalFiles, read, formatVersion));
	}

	@Override
	@Nullable
	public <T> T readOptional(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return wrapped.has(key) ? read(key, reader) : null;
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
	public void writeByteArray(@Nullable String key, @Nullable byte[] value) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.length);

		for (byte v : value) arr.add(v);

		wrapped.add(key, arr);
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
	public <T> void writeList(@Nullable String key, List<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.size());

		for (T v : value) {
			final JsonCodec codec = new JsonCodec(externalFiles, formatVersion);
			writer.accept(v, codec);
			arr.add(codec.unwrap());
		}

		wrapped.add(key, arr);
	}

	@Override
	public <T> void writeOptionalList(@Nullable String key, List<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.size());

		for (T v : value)
			if (v == null)
				arr.add(JsonNull.INSTANCE);
			else {
				final JsonCodec codec = new JsonCodec(externalFiles, formatVersion);
				writer.accept(v, codec);
				arr.add(codec.unwrap());
			}

		wrapped.add(key, arr);
	}

	@Override
	public void writeStringList(@Nullable String key, List<String> value) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.size());

		for (String v : value) arr.add(Objects.requireNonNull(v));

		wrapped.add(key, arr);
	}

	@Override
	public <T> void write(@Nullable String key, T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final JsonCodec codec = new JsonCodec(externalFiles, formatVersion);
		writer.accept(value, codec);
		wrapped.add(key, codec.unwrap());
	}

	@Override
	public <T> void writeOptional(@Nullable String key, @Nullable T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		if (value != null)
			write(key, value, writer);
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

	protected void checkRead() {
		if (!read) throw new UnsupportedOperationException("Codec is write-only");
	}

	protected void checkWrite() {
		if (read) throw new UnsupportedOperationException("Codec is read-only");
	}
}