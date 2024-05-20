package test.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.ParsingStrategy;
import net.syntactickitsune.furblorb.io.TriConsumer;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.Codec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;
import net.syntactickitsune.furblorb.io.codec.JsonCodec;

/**
 * <p>
 * {@code NBTCodec} is an {@link Encoder}/{@link Decoder} hybrid that works with NBT data. (Yes, <i>Minecraft</i>'s data format.)
 * It turns out that when you create a generalized binary data format you tend to arrive at something similar to NBT, so why not
 * do some sequence breaking and just use NBT directly? And that's where we are now.
 * </p>
 * <p>
 * This {@code Codec} is a wrapper around {@link CompoundTag}, unifying Furblorb's serialization API and NBT.
 * On top of this, it takes into account some NBT-specific optimizations, such as writing numbers as smaller data types (where applicable).
 * Combined with NBT's almost-natural GZIP compression, writing {@link Furball Furballs} in NBT results in just under half the file size!
 * </p>
 * @author SyntacticKitsune
 * @see BinaryCodec
 * @see JsonCodec
 */
public class NBTCodec extends Codec {

	/**
	 * The wrapped {@link CompoundTag}.
	 */
	protected final CompoundTag wrapped;

	/**
	 * Constructs a new {@code NBTCodec} with the specified parameters.
	 * @param root The root object.
	 * @param mode The mode that the {@code NBTCodec} should be in.
	 * @throws NullPointerException If {@code root} is {@code null}.
	 */
	public NBTCodec(CompoundTag root, CodecMode mode) {
		super(mode);
		wrapped = Objects.requireNonNull(root, "root");
	}

	/**
	 * Constructs a new {@code NBTCodec} with the specified parameters.
	 * @param root The root object.
	 * @param mode The mode that the {@code NBTCodec} should be in.
	 * @param formatVersion A specific format version to use. This ensures that it's actually set.
	 * @throws NullPointerException If {@code root} is {@code null}.
	 */
	public NBTCodec(CompoundTag root, CodecMode mode, byte formatVersion) {
		this(root, mode);
		this.formatVersion = formatVersion;
	}

	/**
	 * Returns the {@code NBTCodec}'s backing {@code CompoundTag}.
	 * @return The backing {@code CompoundTag}.
	 */
	public CompoundTag unwrap() {
		return wrapped;
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
	public byte readByte(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asByte();
	}

	@Override
	public byte @Nullable [] readByteArray(@Nullable String key) {
		checkRead();
		return wrapped.containsKey(Objects.requireNonNull(key, "key")) ? wrapped.getByteArray(key) : null;
	}

	@Override
	public boolean readBoolean(@Nullable String key) {
		checkRead();
		return wrapped.getBoolean(Objects.requireNonNull(key, "key"));
	}

	@Override
	public short readShort(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asShort();
	}

	@Override
	public int readInt(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asInt();
	}

	@Override
	public long readLong(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asLong();
	}

	@Override
	public float readFloat(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asFloat();
	}

	@Override
	public double readDouble(@Nullable String key) {
		checkRead();
		return wrapped.getNumberTag(Objects.requireNonNull(key, "key")).asDouble();
	}

	@Override
	public UUID readUUID(@Nullable String key) {
		checkRead();
		return UUID.fromString(readString(Objects.requireNonNull(key, "key")));
	}

	@Override
	public String readString(@Nullable String key) {
		checkRead();
		return wrapped.containsKey(Objects.requireNonNull(key, "key")) ? wrapped.getString(key) : ""; // Coerce to "".
	}

	@Override
	public <E extends Enum<E> & INamedEnum> E readEnum(@Nullable String key, Class<E> type) {
		checkRead();
		final E[] vals = type.getEnumConstants();
		final ParsingStrategy.NumberType numberType = numberType(type);

		final int index = switch (numberType) {
			case BYTE -> Byte.toUnsignedInt(readByte(key));
			case SHORT -> Short.toUnsignedInt(readShort(key));
			case INT -> readInt(key);
		};

		if (index < 0 || index >= vals.length)
			throw new FurblorbParsingException("Attempt to access enum constant " + index + " which does not exist (enum: " + type.getName() + ", using " + numberType + " number type)");

		return vals[index];
	}

	@Override
	public <T> List<T> readList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		final ListTag<CompoundTag> arr = wrapped.getListTag(key).asCompoundTagList();
		final List<T> ret = new ArrayList<>(arr.size());

		for (int i = 0; i < arr.size(); i++)
			ret.add(reader.apply(new NBTCodec(arr.get(i), mode, formatVersion)));

		return ret;
	}

	@Override
	public <T> List<@Nullable T> readOptionalList(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		final ListTag<CompoundTag> listTag = wrapped.getListTag(key).asCompoundTagList();
		final List<@Nullable T> ret = new ArrayList<>(listTag.size());

		for (CompoundTag tag : listTag)
			if (!tag.containsKey("__null__"))
				ret.add(reader.apply(new NBTCodec(tag, mode, formatVersion)));
			else
				ret.add(null);

		return ret;
	}

	@Override
	public List<String> readStringList(@Nullable String key) {
		checkRead();
		final ListTag<StringTag> tag = wrapped.getListTag(key).asStringTagList();
		final List<String> ret = new ArrayList<>(tag.size());

		for (StringTag str : tag)
			ret.add(str.getValue());

		return ret;
	}

	@Override
	public <T> T read(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return reader.apply(new NBTCodec(wrapped.getCompoundTag(key), mode, formatVersion));
	}

	@Override
	@Nullable
	public <T> T readOptional(@Nullable String key, Function<Decoder, T> reader) {
		checkRead();
		return wrapped.containsKey(key) ? read(key, reader) : null;
	}

	@Override
	public <T> T readExternal(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) {
		checkRead();
		return reader.apply(this, key);
	}

	@Override
	@Nullable
	public <T> T readExternalOptional(@Nullable String key, BiFunction<Decoder, String, T> reader, Function<byte[], T> externalReader) {
		checkRead();
		return wrapped.containsKey(key) ? reader.apply(this, key) : null;
	}

	@Override
	public void writeByte(@Nullable String key, byte value) {
		checkWrite();
		wrapped.putByte(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeByteArray(@Nullable String key, byte @Nullable [] value) {
		checkWrite();
		if (value != null)
			wrapped.putByteArray(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeBoolean(@Nullable String key, boolean value) {
		checkWrite();
		wrapped.putBoolean(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeShort(@Nullable String key, short value) {
		checkWrite();
		if ((byte) value == value)
			writeByte(key, (byte) value);
		else
			wrapped.putShort(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeInt(@Nullable String key, int value) {
		checkWrite();
		// Optimization: try to write ints as smaller data types when possible.
		if ((byte) value == value)
			writeByte(key, (byte) value);
		else if ((short) value == value)
			writeShort(key, (short) value);
		else
			wrapped.putInt(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeLong(@Nullable String key, long value) {
		checkWrite();
		if ((int) value == value)
			writeInt(key, (int) value);

		wrapped.putLong(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeFloat(@Nullable String key, float value) {
		checkWrite();
		wrapped.putFloat(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeDouble(@Nullable String key, double value) {
		checkWrite();
		wrapped.putDouble(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public void writeUUID(@Nullable String key, UUID value) {
		checkWrite();
		writeString(Objects.requireNonNull(key, "key"), value.toString());
	}

	@Override
	public void writeString(@Nullable String key, String value) {
		checkWrite();
		if (!value.isEmpty())
			wrapped.putString(Objects.requireNonNull(key, "key"), value);
	}

	@Override
	public <E extends Enum<E> & INamedEnum> void writeEnum(@Nullable String key, E value) {
		checkWrite();
		Objects.requireNonNull(value, "value");

		final ParsingStrategy.NumberType numberType = numberType(value.getClass());
		switch (numberType) {
			case BYTE -> writeByte(key, (byte) value.ordinal());
			case SHORT -> writeShort(key, (short) value.ordinal());
			case INT -> writeInt(key, value.ordinal());
		}
	}

	@Override
	public <T> void writeList(@Nullable String key, List<T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final ListTag<CompoundTag> arr = new ListTag<>(CompoundTag.class);

		for (T v : value) {
			final CompoundTag tag = new CompoundTag();
			writer.accept(v, new NBTCodec(tag, mode, formatVersion));
			arr.add(tag);
		}

		wrapped.put(key, arr);
	}

	@Override
	public <T> void writeOptionalList(@Nullable String key, List<@Nullable T> value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final ListTag<CompoundTag> arr = new ListTag<>(CompoundTag.class);

		for (@Nullable T v : value) {
			final CompoundTag tag = new CompoundTag();
			if (v != null)
				writer.accept(v, new NBTCodec(tag, mode, formatVersion));
			else
				tag.putBoolean("__null__", true);
			arr.add(tag);
		}

		wrapped.put(key, arr);
	}

	@Override
	public void writeStringList(@Nullable String key, List<String> value) {
		checkWrite();
		final ListTag<StringTag> arr = new ListTag<>(StringTag.class);

		for (String v : value) arr.add(new StringTag(Objects.requireNonNull(v)));

		wrapped.put(key, arr);
	}

	@Override
	public <T> void write(@Nullable String key, T value, BiConsumer<T, Encoder> writer) {
		checkWrite();
		final CompoundTag tag = new CompoundTag();
		writer.accept(value, new NBTCodec(tag, mode, formatVersion));
		wrapped.put(key, tag);
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
		writer.accept(key, value, this);
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
		if (validate() && wrapped.containsKey(key))
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