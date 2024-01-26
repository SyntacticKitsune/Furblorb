package net.syntactickitsune.furblorb.api.io.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.util.TriConsumer;

/**
 * <p>
 * {@code BinaryCodec} is an {@link Encoder}/{@link Decoder} hybrid that works with binary data.
 * Internally it uses {@link ByteBuffer} instances (since I still have nightmares from the
 * last time I touched {@link java.io.DataInputStream DataInputStream}).
 * It is partially a Java implementation of C#'s
 * <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader"><code>BinaryReader</code></a>
 * and
 * <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binarywriter"><code>BinaryWriter</code></a>
 * classes, and it can {@linkplain #read7BitInt() read} and {@linkplain #write7BitInt(int) write}
 * <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint">7-bit {@code int}s</a>.
 * </p>
 * <p>
 * While {@code BinaryCodec} is primarily designed with the purpose of reading/writing Finmer furballs in mind, it can still
 * be used for other purposes, such as reading data from other C# applications or creating one's own bespoke binary blobs.
 * </p>
 * @author SyntacticKitsune
 * @see JsonCodec
 */
public class BinaryCodec extends Codec {

	private ByteBuffer buf;
	protected final boolean read;

	/**
	 * <p>Constructs a new {@code BinaryCodec} with the specified backing buffer.</p>
	 * <p>If the buffer is to be written to, it is recommended not to keep
	 * a reference to it since the buffer may be resized automatically.
	 * If direct access is required, use {@link #buffer()} to access the {@code BinaryCodec}'s backing buffer.</p>
	 * @param buf The backing buffer.
	 * @param read Whether this {@code BinaryCodec} is read-only versus write-only.
	 * @throws NullPointerException If {@code buf} is {@code null}.
	 */
	public BinaryCodec(ByteBuffer buf, boolean read) {
		Objects.requireNonNull(buf, "buf");
		this.buf = buf;
		this.read = read;
	}

	/**
	 * Constructs a new {@code BinaryCodec} with a 16 kiB little-endian backing buffer.
	 * (Furballs are always written in little-endian.)
	 * @param read Whether this {@code BinaryCodec} is read-only versus write-only.
	 */
	public BinaryCodec(boolean read) {
		this(ByteBuffer.allocate(16318), read);
		buf.order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Returns the {@code BinaryCodec}'s backing buffer.
	 * This buffer may be different from the one the {@code BinaryCodec} was initially setup with,
	 * as the buffer is resized automatically (and must be copied to do so).
	 * @return The backing buffer.
	 */
	public ByteBuffer buffer() {
		return buf;
	}

	/**
	 * Creates and returns a new {@code byte} array with the contents of this {@code BinaryCodec}.
	 * This is preferable to digging it out of the buffer since it may be resized automatically,
	 * and thus the length of the buffer may not equal the number of {@code byte}s written.
	 * @return The new {@code byte} array.
	 */
	public byte[] toByteArray() {
		final byte[] bytes = new byte[buf.position()];
		System.arraycopy(buf.array(), 0, bytes, 0, bytes.length);
		return bytes;
	}

	@Override
	public boolean readCompressedTypes() {
		return true;
	}

	@Override
	public boolean writeCompressedTypes() {
		return true;
	}

	public int position() {
		return buf.position();
	}

	/**
	 * Reads the next {@code byte} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public byte readByte() {
		checkRead();
		return buf.get();
	}

	/**
	 * Writes the given {@code byte} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeByte(byte value) {
		checkWrite(1);
		buf.put(value);
	}

	/**
	 * Reads the next {@code len} {@code byte}s from this {@code BinaryCodec}'s sequence.
	 * @param len The number of {@code byte}s to read.
	 * @return The read values, as a {@code len}-sized {@code byte} array.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public byte[] readBytes(int len) {
		checkRead();
		return readBytes(new byte[len]);
	}

	/**
	 * Reads the next {@code array.length} {@code byte}s from this {@code BinaryCodec}'s sequence -- that is, enough to fill the provided array.
	 * @param array The array to fill.
	 * @return The given array.
	 * @throws NullPointerException If {@code array} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public byte[] readBytes(byte[] array) {
		checkRead();
		buf.get(Objects.requireNonNull(array));
		return array;
	}

	/**
	 * Writes all of the {@code byte}s in the given {@code byte} array to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeBytes(byte[] value) {
		checkWrite(value.length);
		buf.put(value);
	}

	/**
	 * Reads the next (optional) {@code byte} array from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	@Nullable
	public byte[] readByteArray() {
		final int len = readInt();
		if (len < 0) return null;
		return readBytes(len);
	}

	/**
	 * Writes the given (optional) {@code byte} array to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write. May be {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeByteArray(@Nullable byte[] value) {
		if (value == null)
			writeInt(-1);
		else {
			writeInt(value.length);
			writeBytes(value);
		}
	}

	/**
	 * Reads the next {@code boolean} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public boolean readBoolean() {
		checkRead();
		return readByte() != 0;
	}

	/**
	 * Writes the given {@code boolean} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeBoolean(boolean value) {
		checkWrite(1);
		writeByte(value ? (byte) 1 : (byte) 0);
	}

	/**
	 * Reads the next {@code char} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public char readChar() {
		checkRead();
		return buf.getChar();
	}

	/**
	 * Writes the given {@code char} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeChar(char value) {
		checkWrite(2);
		buf.putChar(value);
	}

	/**
	 * Reads the next {@code short} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public short readShort() {
		checkRead();
		return buf.getShort();
	}

	/**
	 * Writes the given {@code short} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeShort(short value) {
		checkWrite(2);
		buf.putShort(value);
	}

	/**
	 * Reads the next {@code int} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public int readInt() {
		checkRead();
		return buf.getInt();
	}

	/**
	 * Writes the given {@code int} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeInt(int value) {
		checkWrite(4);
		buf.putInt(value);
	}

	/**
	 * <p>
	 * Reads the next <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint">seven-bit {@code int}</a>
	 * from this {@code BinaryCodec}'s sequence.
	 * </p>
	 * <p>
	 * A seven-bit {@code int} is a special-encoded {@code int} where the most-significant bit determines whether to read more {@code int}.
	 * The idea is that for small {@code int}s only one {@code byte} needs to be used, which is ideal for lists and strings.
	 * The downside to this approach is that the worst-case means that <i>five</i> {@code byte}s are used,
	 * and negative {@code int}s always encounter this because the most-significant bit describes the sign.
	 * </p>
	 * @return The read value.
	 * @throws FurblorbParsingException If the {@code int} described is too long.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public int read7BitInt() {
		checkRead();
		int ret = 0;
		int shift = 0;
		byte next;

		do {
			if (shift == 5 * 7)
				throw new FurblorbParsingException("Int too long");

			next = buf.get();

			ret |= (next & 0x7F) << shift;
			shift += 7;
		} while ((next & 0x80) != 0);

		return ret;
	}

	/**
	 * <p>
	 * Writes the next <a href="https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint">seven-bit {@code int}</a>
	 * to this {@code BinaryCodec}'s sequence.
	 * </p>
	 * <p>
	 * A seven-bit {@code int} is a special-encoded {@code int} where the most-significant bit determines whether to read more {@code int}.
	 * The idea is that for small {@code int}s only one {@code byte} needs to be used, which is ideal for lists and strings.
	 * The downside to this approach is that the worst-case means that <i>five</i> {@code byte}s are used,
	 * and negative {@code int}s always encounter this because the most-significant bit describes the sign.
	 * </p>
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void write7BitInt(int value) {
		checkWrite(5); // Worst case scenario
		long v = Integer.toUnsignedLong(value);

		while (v >= 0x80) {
			buf.put((byte) (v | 0x80));
			v >>= 7;
		}

		buf.put((byte) v);
	}

	/**
	 * Reads the next {@code long} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public long readLong() {
		checkRead();
		return buf.getLong();
	}

	/**
	 * Writes the given {@code long} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeLong(long value) {
		checkWrite(8);
		buf.putLong(value);
	}

	/**
	 * Reads the next {@code float} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public float readFloat() {
		checkRead();
		return buf.getFloat();
	}

	/**
	 * Writes the given {@code float} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeFloat(float value) {
		checkWrite(4);
		buf.putFloat(value);
	}

	/**
	 * Reads the next {@code double} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public double readDouble() {
		checkRead();
		return buf.getDouble();
	}

	/**
	 * Writes the given {@code double} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeDouble(double value) {
		checkWrite(8);
		buf.putDouble(value);
	}

	/**
	 * Reads the next C#-style {@link UUID} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public UUID readUUID() {
		checkRead();
		final boolean big = buf.order() == ByteOrder.BIG_ENDIAN;

		// The following lines of code likely make no sense, but don't worry: C#'s Guid doesn't either.
		final int a = readInt();
		final short b = readShort();
		final short c = readShort();

		long d = readLong();
		if (!big) d = Long.reverseBytes(d);

		final long mostSig = (Integer.toUnsignedLong(a) << 32) | (Short.toUnsignedLong(b) << 16) | Short.toUnsignedLong(c);
		final long leastSig = d;

		return new UUID(big ? leastSig : mostSig, big ? mostSig : leastSig);
	}

	/**
	 * Writes the given {@link UUID} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeUUID(UUID value) {
		checkWrite(16);
		Objects.requireNonNull(value, "value");

		final boolean big = buf.order() == ByteOrder.BIG_ENDIAN;

		final long mostSig = big ? value.getLeastSignificantBits() : value.getMostSignificantBits();
		final long leastSig = big ? value.getMostSignificantBits() : value.getLeastSignificantBits();

		final int a = (int) (mostSig >> 32);
		final short b = (short) ((mostSig >> 16) & 0xFFFF);
		final short c = (short) (mostSig & 0xFFFF);
		final long d = big ? leastSig : Long.reverseBytes(leastSig);

		writeInt(a);
		writeShort(b);
		writeShort(c);
		writeLong(d);
	}

	/**
	 * Reads the next {@link String} from this {@code BinaryCodec}'s sequence.
	 * @return The read value.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public String readString() {
		checkRead();
		final int len = read7BitInt();

		final byte[] bytes = new byte[len];
		buf.get(bytes);

		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Writes the given {@link String} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public void writeString(String value) {
		checkWrite(0);
		Objects.requireNonNull(value, "value");

		final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

		write7BitInt(bytes.length);
		checkWrite(bytes.length);

		buf.put(bytes);
	}

	/**
	 * Reads the next {@code enum} constant from this {@code BinaryCodec}'s sequence.
	 * The constant will be decoded using an ordinal.
	 * @param <E> The {@code enum} type.
	 * @param type The type of {@code enum} to read. Required to interpret {@code enum} constants correctly.
	 * @return The read value.
	 * @throws NullPointerException If {@code type} is {@code null}.
	 * @throws FurblorbParsingException If the read ordinal is out-of-bounds.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public <E extends Enum<E>> E readEnum(Class<E> type) {
		checkRead();
		final E[] vals = type.getEnumConstants();
		final ParsingStrategy.NumberType numberType = numberType(type);

		final int index = switch (numberType) {
			case BYTE -> Byte.toUnsignedInt(readByte());
			case SHORT -> Short.toUnsignedInt(readShort());
			case INT -> readInt();
		};

		if (index < 0 || index >= vals.length)
			throw new FurblorbParsingException("Attempt to access enum constant " + index + " which does not exist (enum: " + type.getName() + ", using " + numberType + " number type)");

		return vals[index];
	}

	/**
	 * Writes the given {@code enum} constant to this {@code BinaryCodec}'s sequence.
	 * The constant will be encoded using its ordinal.
	 * @param <E> The {@code enum} type.
	 * @param value The value to write.
	 * @throws NullPointerException If {@code value} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public <E extends Enum<E>> void writeEnum(E value) {
		checkWrite(0);
		Objects.requireNonNull(value, "value");

		final ParsingStrategy.NumberType numberType = numberType(value.getClass());
		switch (numberType) {
			case BYTE -> writeByte((byte) value.ordinal());
			case SHORT -> writeShort((short) value.ordinal());
			case INT -> writeInt(value.ordinal());
		}
	}

	/**
	 * Reads the next {@link List} from this {@code BinaryCodec}'s sequence.
	 * @param reader A {@code Function} to read the individual values from this {@code BinaryCodec}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 * @throws FurblorbParsingException If an attempt to read more than 1000 entries is made. This frequently indicates a deserialization bug.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public <T> List<T> readList(Function<Decoder, T> reader) {
		checkRead();

		final int count = readInt();
		if (count > 1000) throw new FurblorbParsingException("Attempt to read " + count + " list entries");

		final List<T> ret = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			ret.add(reader.apply(this));

		return ret;
	}

	/**
	 * Writes the given {@link List} to this {@code BinaryCodec}'s sequence.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public <T> void writeList(List<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writeInt(value.size());
		for (T elem : value)
			writer.accept(elem, this);
	}

	/**
	 * Reads the next {@link List} from this {@code BinaryCodec}'s sequence.
	 * The {@code List} may contain {@code null} values.
	 * @param reader A {@code Function} to read the individual values from this {@code BinaryCodec}'s sequence.
	 * @return The read list.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public <T> List<@Nullable T> readOptionalList(Function<Decoder, T> reader) {
		checkRead();
		return readList(dec -> dec.readBoolean(null) ? reader.apply(dec) : null);
	}

	/**
	 * Writes the given {@link List} to this {@code BinaryCodec}'s sequence.
	 * The {@code List} may contain {@code null} values.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the values within the list.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public <T> void writeOptionalList(List<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writeList(value, (val, enc) -> {
			if (val == null)
				enc.writeBoolean(null, false);
			else {
				enc.writeBoolean(null, true);
				writer.accept(val, enc);
			}
		});
	}

	/**
	 * Reads the next {@code Object} from this {@code BinaryCodec}'s sequence using the provided reader.
	 * @param <T> The kind of {@code Object} to read.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public <T> T read(Function<Decoder, T> reader) {
		checkRead();
		return reader.apply(this);
	}

	/**
	 * Writes the given {@code Object} to this {@code BinaryCodec}'s sequence using the provided writer.
	 * @param value The value to write.
	 * @param writer A {@link BiConsumer} to handle writing the value.
	 * @throws NullPointerException If {@code value} or {@code writer} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public <T> void write(T value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writer.accept(value, this);
	}

	/**
	 * Reads the next {@code Object} from this {@code BinaryCodec}'s sequence using the provided reader, if one exists.
	 * Otherwise, returns {@code null}.
	 * @param <T> The kind of {@code Object} to read.
	 * @param reader A {@code Function} to read the desired value.
	 * @return The read value. May be {@code null}.
	 * @throws NullPointerException If {@code reader} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is write-only.
	 */
	public <T> @Nullable T readOptional(Function<Decoder, T> reader) {
		checkRead();
		return readBoolean() ? read(reader) : null;
	}

	/**
	 * Writes the given {@code Object} -- which may be {@code null} -- to this {@code BinaryCodec}'s sequence using the provided writer.
	 * A {@code null} value will <i>not</i> be passed to the writer.
	 * @param value The value to write. May be {@code null}.
	 * @param writer A {@link BiConsumer} to handle writing the (non-{@code null}) value.
	 * @throws NullPointerException If {@code writer} is {@code null}.
	 * @throws UnsupportedOperationException If this codec is read-only.
	 */
	public <T> void writeOptional(@Nullable T value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		if (value != null) {
			writeBoolean(true);
			write(value, writer);
		} else
			writeBoolean(false);
	}

	protected void checkRead() {
		if (!read) throw new UnsupportedOperationException("Codec is write-only");
	}

	protected void checkWrite(int length) {
		if (read) throw new UnsupportedOperationException("Codec is read-only");

		if (buf.remaining() < length) {
			final ByteBuffer old = buf;
			buf = ByteBuffer.allocate(old.capacity() + Math.max(length, old.capacity()));
			buf.order(old.order());
			old.flip();
			buf.put(old);
		}
	}

	// ===== OVERRIDES =====

	@Override
	public byte readByte(@Nullable String key) { return readByte(); }

	@Override
	public byte[] readByteArray(@Nullable String key) { return readByteArray(); }

	@Override
	public boolean readBoolean(@Nullable String key) { return readBoolean(); }

	@Override
	public short readShort(@Nullable String key) { return readShort(); }

	@Override
	public int readInt(@Nullable String key) { return readInt(); }

	@Override
	public long readLong(@Nullable String key) { return readLong(); }

	@Override
	public float readFloat(@Nullable String key) { return readFloat(); }

	@Override
	public double readDouble(@Nullable String key) { return readDouble(); }

	@Override
	public UUID readUUID(@Nullable String key) { return readUUID(); }

	@Override
	public String readString(@Nullable String key) { return readString(); }

	@Override
	public <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type) { return readEnum(type); }

	@Override
	public <T> List<T> readList(@Nullable String key, Function<Decoder, T> reader) { return readList(reader); }

	@Override
	public <T> List<@Nullable T> readOptionalList(@Nullable String key, Function<Decoder, T> reader) { return readOptionalList(reader); }

	@Override
	public List<String> readStringList(@Nullable String key) { return readList(dec -> dec.readString(null)); }

	@Override
	public <T> T read(@Nullable String key, Function<Decoder, T> reader) { return read(reader); }

	@Override
	public <T> @Nullable T readOptional(@Nullable String key, Function<Decoder, T> reader) { return readOptional(reader); }

	@Override
	public <T> T readExternal(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) { return read(dec -> reader.apply(dec, key)); }

	@Override
	@Nullable
	public <T> T readExternalOptional(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) { return readOptional(dec -> reader.apply(dec, key)); }

	@Override
	public void writeByte(@Nullable String key, byte value) { writeByte(value); }

	@Override
	public void writeByteArray(@Nullable String key, @Nullable byte[] value) { writeByteArray(value); }

	@Override
	public void writeBoolean(@Nullable String key, boolean value) { writeBoolean(value); }

	@Override
	public void writeShort(@Nullable String key, short value) { writeShort(value); }

	@Override
	public void writeInt(@Nullable String key, int value) { writeInt(value); }

	@Override
	public void writeLong(@Nullable String key, long value) { writeLong(value); }

	@Override
	public void writeFloat(@Nullable String key, float value) { writeFloat(value); }

	@Override
	public void writeDouble(@Nullable String key, double value) { writeDouble(value); }

	@Override
	public void writeUUID(@Nullable String key, UUID value) { writeUUID(value); }

	@Override
	public void writeString(@Nullable String key, String value) { writeString(value); }

	@Override
	public <E extends Enum<E> & INamedEnum> void writeEnum(@Nullable String key, E value) { writeEnum(value); }

	@Override
	public <T> void writeList(@Nullable String key, List<T> value, BiConsumer<T, Encoder> writer) { writeList(value, writer); }

	@Override
	public <T> void writeOptionalList(@Nullable String key, List<@Nullable T> value, BiConsumer<T, Encoder> writer) { writeOptionalList(value, writer); }

	@Override
	public void writeStringList(@Nullable String key, List<String> value) { writeList(value, (v, enc) -> enc.writeString(null, v)); }

	@Override
	public <T> void write(@Nullable String key, T value, BiConsumer<T, Encoder> writer) { writer.accept(value, this); }

	@Override
	public <T> void writeOptional(@Nullable String key, @Nullable T value, BiConsumer<T, Encoder> writer) { writeOptional(value, writer); }

	@Override
	public <T> void writeExternal(@Nullable String key, T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) { writer.accept(key, value, this); }

	@Override
	public <T> void writeExternalOptional(@Nullable String key, @Nullable T value, TriConsumer<String, T, Encoder> writer, Function<T, byte[]> externalWriter) { writeOptional(value, (val, enc) -> writer.accept(key, val, enc)); }

	@Override
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException {}
}