package net.syntactickitsune.furblorb.io.codec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
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

/**
 * <p>
 * {@code JsonArrayCodec} is a {@link SequenceEncoder}/{@link SequenceDecoder} hybrid that works with json data.
 * Internally it uses {@link JsonArray} instances.
 * {@code JsonArrayCodec} is intended to be used alongside {@link JsonCodec}, as the means to write the contents of {@link JsonArray JsonArrays}.
 * </p>
 * @author SyntacticKitsune
 * @since 2.0.0
 * @see JsonCodec
 */
public class JsonArrayCodec extends SequenceCodec {

	/**
	 * The wrapped {@link JsonArray}.
	 */
	protected JsonArray wrapped;

	/**
	 * The {@link ExternalFileHandler}. In many cases this will be an {@link net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ExtendedExternalFileHandler ExtendedExternalFileHandler}.
	 */
	@Nullable
	protected final ExternalFileHandler externalFiles;

	protected boolean encodeZeroIdsAsNull;

	/**
	 * The index into the {@linkplain #wrapped wrapped Json array}.
	 */
	protected int index;

	/**
	 * Constructs a new {@code JsonArrayCodec} with the specified parameters.
	 * @param array The array.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonArrayCodec} to read and write them inline.
	 * @param mode The mode that the {@code JsonArrayCodec} should be in.
	 * @throws NullPointerException If {@code array} or {@code mode} are {@code null}.
	 */
	public JsonArrayCodec(JsonArray array, @Nullable ExternalFileHandler externalFiles, CodecMode mode) {
		super(mode);
		wrapped = Objects.requireNonNull(array, "array");
		this.externalFiles = externalFiles;
	}

	/**
	 * Constructs a new {@code JsonArrayCodec} with the specified parameters.
	 * @param array The array.
	 * @param externalFiles A handler for external files. Passing in {@code null} will cause the {@code JsonArrayCodec} to read and write them inline.
	 * @param mode The mode that the {@code JsonArrayCodec} should be in.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 * @throws NullPointerException If {@code array} or {@code mode} are {@code null}.
	 */
	public JsonArrayCodec(JsonArray array, @Nullable ExternalFileHandler externalFiles, CodecMode mode, byte formatVersion) {
		this(array, externalFiles, mode);
		this.formatVersion = formatVersion;
	}

	public boolean encodeZeroIdsAsNull() {
		return encodeZeroIdsAsNull;
	}

	public JsonArrayCodec setEncodeZeroIdsAsNull(boolean value) {
		encodeZeroIdsAsNull = value;
		return this;
	}

	@Override
	public boolean hasRemaining() {
		return index < wrapped.size();
	}

	@Override
	public boolean readCompressedTypes() {
		return false;
	}

	@Override
	public boolean writeCompressedTypes() {
		return false;
	}

	protected JsonElement next() {
		return wrapped.get(index++);
	}

	@Override
	public byte readByte() {
		checkRead();
		return next().getAsByte();
	}

	@Override
	public byte[] readBytes(byte[] array) {
		checkRead();
		final byte[] a2 = readByteArray();
		System.arraycopy(a2, 0, array, 0, array.length);
		return array;
	}

	private byte[] readByteArray(JsonArray arr) {
		final byte[] ret = new byte[arr.size()];

		for (int i = 0; i < arr.size(); i++)
			ret[i] = arr.get(i).getAsByte();

		return ret;
	}

	@Override
	public byte[] readByteArray() {
		checkRead();

		return readByteArray(next().getAsJsonArray());
	}

	@Override
	public byte @Nullable [] readOptionalByteArray() {
		checkRead();

		final JsonElement elem = next();
		if (elem.isJsonNull()) return null;

		return readByteArray(elem.getAsJsonArray());
	}

	@Override
	public boolean readBoolean() {
		checkRead();
		return next().getAsBoolean();
	}

	@SuppressWarnings("deprecation")
	@Override
	public char readChar() {
		checkRead();
		return next().getAsCharacter();
	}

	@Override
	public short readShort() {
		checkRead();
		return next().getAsShort();
	}

	@Override
	public int readInt() {
		checkRead();
		return next().getAsInt();
	}

	@Override
	public int read7BitInt() {
		checkRead();
		return readInt();
	}

	@Override
	public long readLong() {
		checkRead();
		return next().getAsLong();
	}

	@Override
	public float readFloat() {
		checkRead();
		return next().getAsFloat();
	}

	@Override
	public double readDouble() {
		checkRead();
		return next().getAsDouble();
	}

	@Override
	public UUID readUUID() {
		checkRead();

		final JsonElement next = next();
		if (encodeZeroIdsAsNull && next.isJsonNull())
			return JsonCodec.ZERO_UUID;

		return UUID.fromString(next.getAsString());
	}

	@Override
	public String readString() {
		checkRead();
		final JsonElement next = next();
		return next == null ? "" : next.getAsString(); // Coerce to "".
	}

	@Override
	public String readFixedLengthString(int length) {
		checkRead();
		return readString().substring(0, length);
	}

	@Override
	public <E extends Enum<E> & INamedEnum> E readEnum(Class<E> type, Function<E, String> idFunction) {
		checkRead();
		return JsonCodec.getConstantById(readString(), type, idFunction, formatVersion());
	}

	@Override
	public <T> List<T> readObjectList(Function<Decoder, T> reader) {
		checkRead();
		return readListOf(dec -> dec.readObject(reader));
	}

	@Override
	public <T> List<@Nullable T> readOptionalObjectList(Function<Decoder, T> reader) {
		checkRead();
		return readListOf(dec -> dec.readOptionalObject(reader));
	}

	@Override
	public <T> List<T> readListOf(Function<SequenceDecoder, T> reader) {
		checkRead();
		final JsonArray array = next().getAsJsonArray();
		final JsonArrayCodec codec = popArray(array);

		final List<T> ret = new ArrayList<>(array.size());
		for (int i = 0; i < array.size(); i++)
			ret.add(reader.apply(codec));

		return ret;
	}

	@Override
	public <T> T readObject(Function<Decoder, T> reader) {
		checkRead();
		final JsonObject obj = next().getAsJsonObject();
		final JsonCodec codec = popObject(obj);

		return reader.apply(codec);
	}

	@Override
	public <T> @Nullable T readOptionalObject(Function<Decoder, T> reader) {
		checkRead();
		final JsonElement elem = next();
		if (elem.isJsonNull()) return null;

		final JsonCodec codec = popObject(elem.getAsJsonObject());

		return reader.apply(codec);
	}

	@Override
	public void writeByte(byte value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeBytes(byte[] value) {
		checkWrite();
		final JsonArray array = new JsonArray(value.length);
		for (byte b : value) array.add(b);
		wrapped.add(array);
		index++;
	}

	@Override
	public void writeByteArray(byte[] value) {
		checkWrite();
		writeBytes(value);
	}

	@Override
	public void writeOptionalByteArray(byte @Nullable [] value) {
		checkWrite();
		if (value != null)
			writeBytes(value);
		else {
			wrapped.add(JsonNull.INSTANCE);
			index++;
		}
	}

	@Override
	public void writeBoolean(boolean value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeChar(char value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeShort(short value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeInt(int value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void write7BitInt(int value) {
		checkWrite();
		writeInt(value);
	}

	@Override
	public void writeLong(long value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeFloat(float value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeDouble(double value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeUUID(UUID value) {
		checkWrite();

		if (encodeZeroIdsAsNull && value.equals(JsonCodec.ZERO_UUID))
			wrapped.add(JsonNull.INSTANCE);
		else
			writeString(value.toString());
	}

	@Override
	public void writeString(String value) {
		checkWrite();
		wrapped.add(value);
		index++;
	}

	@Override
	public void writeFixedLengthString(String value) {
		checkWrite();
		writeString(value);
	}

	@Override
	public <E extends Enum<E> & INamedEnum> void writeEnum(E value, Function<E, String> idFunction) {
		checkWrite();
		if (value.formatVersion() > formatVersion()) throw new FurblorbParsingException("Cannot encode " + value + " for format version " + formatVersion() + " as it is only available in " + value.formatVersion() + " and higher");
		writeString(idFunction.apply(value));
	}

	@Override
	public <T> void writeObjectList(Collection<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		writeListOf(value, (enc, v) -> enc.writeObject(v, writer));
	}

	@Override
	public <T> void writeOptionalObjectList(Collection<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		writeListOf(value, (enc, v) -> enc.writeOptionalObject(v, writer));
	}

	@Override
	public <T> void writeListOf(Collection<T> value, BiConsumer<SequenceEncoder, T> writer) {
		checkWrite();
		final JsonArray arr = new JsonArray(value.size());
		final JsonArrayCodec codec = popArray(arr);

		for (T v : value)
			writer.accept(codec, v);

		wrapped.add(arr);
		index++;
	}

	@Override
	public <T> void writeObject(T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final JsonCodec codec = popObject(null);
		writer.accept(value, codec);
		wrapped.add(codec.wrapped);
		index++;
	}

	@Override
	public <T> void writeOptionalObject(@Nullable T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		if (value != null)
			writeObject(value, writer);
		else {
			wrapped.add(JsonNull.INSTANCE);
			index++;
		}
	}

	/**
	 * Checks to see make sure read access is supported.
	 * @throws UnsupportedOperationException If the codec is write-only.
	 */
	protected void checkRead() {
		if (!mode.canRead()) throw new UnsupportedOperationException("Codec is write-only");
		if (!hasRemaining()) throw new IllegalStateException("End of data");
	}

	/**
	 * Checks to see make sure write access is supported.
	 * @throws UnsupportedOperationException If the codec is read-only.
	 */
	protected void checkWrite() {
		if (!mode.canWrite()) throw new UnsupportedOperationException("Codec is read-only");
	}

	protected JsonCodec popObject(@Nullable JsonObject wrapped) {
		final JsonCodec ret = wrapped == null ? new JsonCodec(externalFiles, formatVersion()) : new JsonCodec(wrapped, externalFiles, mode, formatVersion());
		ret.encodeZeroIdsAsNull = encodeZeroIdsAsNull;
		return ret;
	}

	protected JsonArrayCodec popArray(JsonArray wrapped) {
		final JsonArrayCodec ret = new JsonArrayCodec(wrapped, externalFiles, mode, formatVersion());
		ret.encodeZeroIdsAsNull = encodeZeroIdsAsNull;
		return ret;
	}
}