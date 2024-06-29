package net.syntactickitsune.furblorb.io.codec;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;

/**
 * The {@code DelegatingSequenceCodec} class serves as a way to easily tweak the behavior of other {@link Codec Codecs}
 * by overriding select methods and letting all the others defer to the backing {@code Codec}.
 * In other words, this class overrides all the {@link SequenceCodec} methods to delegate to another {@code SequenceCodec},
 * and can be used as a starting point for codecs that build upon others.
 * @author SyntacticKitsune
 */
public class DelegatingSequenceCodec extends SequenceCodec {

	/**
	 * The delegate {@code SequenceCodec}, as set in the constructor.
	 */
	protected final SequenceCodec delegate;

	/**
	 * Constructs a new {@code DelegatingSequenceCodec} from the specified codec.
	 * @param delegate The delegate codec to use for all operations by default.
	 * @throws NullPointerException If {@code delegate} is {@code null}.
	 */
	public DelegatingSequenceCodec(SequenceCodec delegate) {
		super(delegate.mode);
		this.delegate = delegate;
		formatVersion = delegate.formatVersion;
		validate = delegate.validate;
	}

	// Overrides for settings. The settings are kept separate (though inherited) by default.
	// Copy these into your implementation if you want to delegate those too.
	/*
	@Override public byte formatVersion() { return delegate.formatVersion(); }
	@Override public void setFormatVersion(byte value) { delegate.setFormatVersion(value); }
	@Override public boolean validate() { return delegate.validate(); }
	@Override public void setValidate(boolean value) { delegate.setValidate(value); }
	*/

	@Override public boolean hasRemaining() { return delegate.hasRemaining(); }
	@Override public boolean readCompressedTypes() { return delegate.readCompressedTypes(); }
	@Override public boolean writeCompressedTypes() { return delegate.writeCompressedTypes(); }

	@Override public byte readByte() { return delegate.readByte(); }
	@Override public byte[] readBytes(int len) { return delegate.readBytes(len); }
	@Override public byte[] readBytes(byte[] array) { return delegate.readBytes(array); }
	@Override public byte[] readByteArray() { return delegate.readByteArray(); }
	@Override public byte @Nullable [] readOptionalByteArray() { return delegate.readOptionalByteArray(); }
	@Override public boolean readBoolean() { return delegate.readBoolean(); }
	@Override public char readChar() { return delegate.readChar(); }
	@Override public short readShort() { return delegate.readShort(); }
	@Override public int readInt() { return delegate.readInt(); }
	@Override public int read7BitInt() { return delegate.read7BitInt(); }
	@Override public int readCompressedInt(@Nullable String key) { return delegate.readCompressedInt(key); }
	@Override public long readLong() { return delegate.readLong(); }
	@Override public float readFloat() { return delegate.readFloat(); }
	@Override public double readDouble() { return delegate.readDouble(); }
	@Override public UUID readUUID() { return delegate.readUUID(); }
	@Override public String readString() { return delegate.readString(); }
	@Override public String readFixedLengthString(int length) { return delegate.readFixedLengthString(length); }
	@Override public <E extends Enum<E> & INamedEnum> E readEnum(Class<E> type) { return delegate.readEnum(type); }
	@Override public <T> List<T> readObjectList(Function<Decoder, T> reader) { return delegate.readObjectList(reader); }
	@Override public <T> List<@Nullable T> readOptionalObjectList(Function<Decoder, T> reader) { return delegate.readOptionalObjectList(reader); }
	@Override public <T> List<T> readListOf(Function<SequenceDecoder, T> reader) { return delegate.readListOf(reader); }
	@Override public <T> T readObject(Function<Decoder, T> reader) { return delegate.readObject(reader); }
	@Override public <T> @Nullable T readOptionalObject(Function<Decoder, T> reader) { return delegate.readOptionalObject(reader); }

	@Override public void writeByte(byte value) { delegate.writeByte(value); }
	@Override public void writeBytes(byte[] value) { delegate.writeBytes(value); }
	@Override public void writeByteArray(byte[] value) { delegate.writeByteArray(value); }
	@Override public void writeOptionalByteArray(byte @Nullable [] value) { delegate.writeOptionalByteArray(value); }
	@Override public void writeBoolean(boolean value) { delegate.writeBoolean(value); }
	@Override public void writeChar(char value) { delegate.writeChar(value); }
	@Override public void writeShort(short value) { delegate.writeShort(value); }
	@Override public void writeInt(int value) { delegate.writeInt(value); }
	@Override public void write7BitInt(int value) { delegate.write7BitInt(value); }
	@Override public void writeCompressedInt(@Nullable String key, int value) { delegate.writeCompressedInt(key, value); }
	@Override public void writeLong(long value) { delegate.writeLong(value); }
	@Override public void writeFloat(float value) { delegate.writeFloat(value); }
	@Override public void writeDouble(double value) { delegate.writeDouble(value); }
	@Override public void writeUUID(UUID value) { delegate.writeUUID(value); }
	@Override public void writeString(String value) { delegate.writeString(value); }
	@Override public void writeFixedLengthString(String value) { delegate.writeFixedLengthString(value); }
	@Override public <E extends Enum<E> & INamedEnum> void writeEnum(E value) { delegate.writeEnum(value); }
	@Override public <T> void writeObjectList(Collection<T> value, BiConsumer<T, Encoder> writer) { delegate.writeObjectList(value, writer); }
	@Override public <T> void writeOptionalObjectList(Collection<@Nullable T> value, BiConsumer<T, Encoder> writer) { delegate.writeOptionalObjectList(value, writer); }
	@Override public <T> void writeListOf(Collection<T> value, BiConsumer<SequenceEncoder, T> writer) { delegate.writeListOf(value, writer); }
	@Override public <T> void writeObject(T value, BiConsumer<T, Encoder> writer) { delegate.writeObject(value, writer); }
	@Override public <T> void writeOptionalObject(@Nullable T value, BiConsumer<T, Encoder> writer) { delegate.writeOptionalObject(value, writer); }

	@Override public void assertDoesNotExist(String key, String message) throws FurblorbParsingException { delegate.assertDoesNotExist(key, message); }
}