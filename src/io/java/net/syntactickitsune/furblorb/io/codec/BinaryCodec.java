package net.syntactickitsune.furblorb.io.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.ParsingStrategy;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;

/**
 * <p>
 * {@code BinaryCodec} is a {@link SequenceEncoder}/{@link SequenceDecoder} hybrid that works with binary data.
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
 * be used for other purposes, such as reading data from other C# applications or creating one's own bespoke binary files.
 * </p>
 * @author SyntacticKitsune
 * @see JsonCodec
 */
public class BinaryCodec extends SequenceCodec {

	private ByteBuffer buf;

	/**
	 * <p>Constructs a new {@code BinaryCodec} with the specified backing buffer.</p>
	 * <p>If the codec is to be written to, it is recommended <i>not</i> to keep
	 * a reference to the buffer since it may be resized automatically.
	 * If direct access is required, use {@link #buffer()} to access the {@code BinaryCodec}'s backing buffer.</p>
	 * @param buf The backing buffer.
	 * @param mode The mode that the {@code BinaryCodec} should be in.
	 * @throws NullPointerException If {@code buf} or {@code mode} are {@code null}.
	 */
	public BinaryCodec(ByteBuffer buf, CodecMode mode) {
		super(mode);
		Objects.requireNonNull(buf, "buf");
		this.buf = buf;
	}

	/**
	 * <p>Constructs a new {@code BinaryCodec} with the specified backing array.
	 * A {@link ByteBuffer} wrapping the array will be used, with a default byte ordering of {@link ByteOrder#LITTLE_ENDIAN}.</p>
	 * <p>If the codec is to be written to, it is recommended <i>not</i> to keep
	 * a reference to the array since it may be resized automatically.
	 * If direct access is required, use {@link #buffer()} to access the {@code BinaryCodec}'s backing buffer.</p>
	 * @param bytes The initial backing array.
	 * @param mode The mode that the {@code BinaryCodec} should be in.
	 * @throws NullPointerException If {@code bytes} or {@code mode} are {@code null}.
	 */
	public BinaryCodec(byte[] bytes, CodecMode mode) {
		this(ByteBuffer.wrap(Objects.requireNonNull(bytes, "bytes")).order(ByteOrder.LITTLE_ENDIAN), mode);
	}

	/**
	 * Constructs a new {@code BinaryCodec} with a 16 kiB little-endian backing buffer.
	 * @param mode The mode that the {@code BinaryCodec} should be in.
	 * @throws NullPointerException If {@code mode} is {@code null}.
	 */
	public BinaryCodec(CodecMode mode) {
		this(ByteBuffer.allocate(16318), mode);
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

	/**
	 * Returns the current position of the {@code BinaryCodec}'s backing buffer.
	 * @return The current position of the backing buffer.
	 */
	public int position() {
		return buf.position();
	}

	@Override
	public boolean hasRemaining() {
		return buf.hasRemaining();
	}

	@Override
	public boolean readCompressedTypes() {
		return true;
	}

	@Override
	public boolean writeCompressedTypes() {
		return true;
	}

	@Override
	public byte readByte() {
		checkRead();
		return buf.get();
	}

	@Override
	public void writeByte(byte value) {
		checkWrite(1);
		buf.put(value);
	}

	@Override
	public byte[] readBytes(byte[] array) {
		checkRead();
		buf.get(Objects.requireNonNull(array));
		return array;
	}

	@Override
	public void writeBytes(byte[] value) {
		checkWrite(value.length);
		buf.put(value);
	}

	@Override
	public byte[] readByteArray() {
		return readBytes(readInt());
	}

	@Override
	public void writeByteArray(byte[] value) {
		writeInt(value.length);
		writeBytes(value);
	}

	@Override
	public byte @Nullable [] readOptionalByteArray() {
		final int len = readInt();
		if (len < 0) return null;
		return readBytes(len);
	}

	@Override
	public void writeOptionalByteArray(byte @Nullable [] value) {
		if (value == null)
			writeInt(-1);
		else
			writeByteArray(value);
	}

	@Override
	public boolean readBoolean() {
		checkRead();
		return readByte() != 0;
	}

	@Override
	public void writeBoolean(boolean value) {
		checkWrite(1);
		writeByte(value ? (byte) 1 : (byte) 0);
	}

	@Override
	public char readChar() {
		checkRead();
		return buf.getChar();
	}

	@Override
	public void writeChar(char value) {
		checkWrite(2);
		buf.putChar(value);
	}

	@Override
	public short readShort() {
		checkRead();
		return buf.getShort();
	}

	@Override
	public void writeShort(short value) {
		checkWrite(2);
		buf.putShort(value);
	}

	@Override
	public int readInt() {
		checkRead();
		return buf.getInt();
	}

	@Override
	public void writeInt(int value) {
		checkWrite(4);
		buf.putInt(value);
	}

	@Override
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

	@Override
	public void write7BitInt(int value) {
		checkWrite(5); // Worst case scenario
		long v = Integer.toUnsignedLong(value);

		while (v >= 0x80) {
			buf.put((byte) (v | 0x80));
			v >>= 7;
		}

		buf.put((byte) v);
	}

	@Override
	public long readLong() {
		checkRead();
		return buf.getLong();
	}

	@Override
	public void writeLong(long value) {
		checkWrite(8);
		buf.putLong(value);
	}

	@Override
	public float readFloat() {
		checkRead();
		return buf.getFloat();
	}

	@Override
	public void writeFloat(float value) {
		checkWrite(4);
		buf.putFloat(value);
	}

	@Override
	public double readDouble() {
		checkRead();
		return buf.getDouble();
	}

	@Override
	public void writeDouble(double value) {
		checkWrite(8);
		buf.putDouble(value);
	}

	@Override
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

	@Override
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

	@Override
	public String readString() {
		checkRead();
		final int len = read7BitInt();

		final byte[] bytes = new byte[len];
		buf.get(bytes);

		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public void writeString(String value) {
		checkWrite(0);
		Objects.requireNonNull(value, "value");

		final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

		write7BitInt(bytes.length);
		checkWrite(bytes.length);

		buf.put(bytes);
	}

	@Override
	public String readFixedLengthString(int length) {
		checkRead();
		if (length < 0) throw new IllegalArgumentException("Length must be positive: " + length);

		final byte[] bytes = new byte[length];
		buf.get(bytes);

		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public void writeFixedLengthString(String value) {
		Objects.requireNonNull(value, "value");

		final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		checkWrite(bytes.length);
		buf.put(bytes);
	}

	@Override
	public <E extends Enum<E> & INamedEnum> E readEnum(Class<E> type) {
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

	@Override
	public <E extends Enum<E> & INamedEnum> void writeEnum(E value) {
		checkWrite(0);
		Objects.requireNonNull(value, "value");

		final ParsingStrategy.NumberType numberType = numberType(value.getClass());
		switch (numberType) {
			case BYTE -> writeByte((byte) value.ordinal());
			case SHORT -> writeShort((short) value.ordinal());
			case INT -> writeInt(value.ordinal());
		}
	}

	@Override
	public <T> List<T> readObjectList(Function<Decoder, T> reader) {
		checkRead();
		return readListOf(dec -> dec.readObject(reader));
	}

	@Override
	public <T> void writeObjectList(Collection<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writeListOf(value, (enc, v) -> enc.writeObject(v, writer));
	}

	@Override
	public <T> List<@Nullable T> readOptionalObjectList(Function<Decoder, T> reader) {
		checkRead();
		return readObjectList(dec -> dec.readBoolean(null) ? reader.apply(dec) : null);
	}

	@Override
	public <T> void writeOptionalObjectList(Collection<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writeObjectList(value, (val, enc) -> {
			if (val == null)
				enc.writeBoolean(null, false);
			else {
				enc.writeBoolean(null, true);
				writer.accept(val, enc);
			}
		});
	}

	@Override
	public <T> List<T> readListOf(Function<SequenceDecoder, T> reader) {
		checkRead();

		final int count = readInt();
		if (count > 1000) throw new FurblorbParsingException("Attempt to read " + count + " list entries");

		final List<T> ret = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			ret.add(reader.apply(this));

		return ret;
	}

	@Override
	public <T> void writeListOf(Collection<T> value, BiConsumer<SequenceEncoder, T> writer) {
		checkWrite(0);
		writeInt(value.size());
		for (T elem : value)
			writer.accept(this, elem);
	}

	@Override
	public <T> T readObject(Function<Decoder, T> reader) {
		checkRead();
		return reader.apply(this);
	}

	@Override
	public <T> void writeObject(T value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		writer.accept(value, this);
	}

	@Override
	public <T> @Nullable T readOptionalObject(Function<Decoder, T> reader) {
		checkRead();
		return readBoolean() ? readObject(reader) : null;
	}

	@Override
	public <T> void writeOptionalObject(@Nullable T value, BiConsumer<T, Encoder> writer) {
		checkWrite(0);
		if (value != null) {
			writeBoolean(true);
			writeObject(value, writer);
		} else
			writeBoolean(false);
	}

	/**
	 * Checks to see make sure read access is supported.
	 * @throws UnsupportedOperationException If the codec is write-only.
	 */
	protected void checkRead() {
		if (!mode.canRead()) throw new UnsupportedOperationException("Codec is write-only");
	}

	/**
	 * Checks to see make sure write access is supported and ensures that the codec has capacity for at least the specified number of bytes.
	 * @param length The number of bytes to ensure capacity for.
	 * @throws UnsupportedOperationException If the codec is read-only.
	 */
	protected void checkWrite(int length) {
		if (!mode.canWrite()) throw new UnsupportedOperationException("Codec is read-only");

		if (buf.remaining() < length) {
			final ByteBuffer old = buf;
			buf = ByteBuffer.allocate(old.capacity() + Math.max(length, old.capacity()));
			buf.order(old.order());
			old.flip();
			buf.put(old);
		}
	}
}