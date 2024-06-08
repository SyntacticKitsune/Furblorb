package net.syntactickitsune.furblorb.finmer.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.FinmerSaveData;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;

/**
 * <p>
 * {@code PropertyContainer} is a container for key-value mappings of case-insensitive {@link String Strings} to
 * {@code boolean}s, {@code byte} arrays, {@code float}s, {@code int}s, {@code String}s, and of course, {@code PropertyContainer}s.
 * These can be set and queried in a manner similar to {@link Map}.
 * </p>
 * <p>
 * {@code PropertyContainer} is Furblorb's version of Finmer's {@code PropertyBag},
 * a class used extensively in save data to store object and scene data, as well as script variables.
 * See {@link FinmerSaveData} for details on this.
 * </p>
 * @since 2.0.0
 */
// "Can we have NBT?"
// "We have NBT at home."
// The NBT at home:
public final class PropertyContainer {

	private final Set<String> booleanProps = new LinkedHashSet<>();
	private final Map<String, ByteArray> byteProps = new LinkedHashMap<>();
	private final Map<String, Float> floatProps = new LinkedHashMap<>();
	private final Map<String, Integer> intProps = new LinkedHashMap<>();
	private final Map<String, String> stringProps = new LinkedHashMap<>();

	/**
	 * Constructs a new, empty {@code PropertyContainer}.
	 */
	public PropertyContainer() {}

	/**
	 * Constructs a new {@code PropertyContainer} with the contents of the specified {@code PropertyContainer}.
	 * @param other The {@code PropertyContainer} to copy the contents of.
	 * @throws NullPointerException If {@code other} is {@code null}.
	 */
	public PropertyContainer(PropertyContainer other) {
		booleanProps.addAll(other.booleanProps);
		floatProps.putAll(other.floatProps);
		intProps.putAll(other.intProps);
		stringProps.putAll(other.stringProps);

		for (var entry : other.byteProps.entrySet())
			byteProps.put(entry.getKey(), entry.getValue());
	}

	/**
	 * Decodes the {@code PropertyContainer} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PropertyContainer(Decoder in) {
		for (String key : in.readListOf("BooleanProperties", SequenceDecoder::readString))
			booleanProps.add(checkKey(key));

		final List<ByteProperty> bps = in.readObjectList("ByteProperties", ByteProperty::read);
		for (ByteProperty prop : bps)
			byteProps.put(checkKey(prop.key), new ByteArray(prop.value));

		final List<IntProperty> ips = in.readObjectList("IntProperties", IntProperty::new);
		for (IntProperty prop : ips)
			intProps.put(checkKey(prop.key), prop.value);

		final List<FloatProperty> fps = in.readObjectList("FloatProperties", FloatProperty::new);
		for (FloatProperty prop : fps)
			floatProps.put(checkKey(prop.key), prop.value);

		final List<StringProperty> sps = in.readObjectList("StringProperties", StringProperty::new);
		for (StringProperty prop : sps)
			stringProps.put(checkKey(prop.key), prop.value);
	}

	private PropertyContainer(byte[] bytes) {
		this(new BinaryCodec(bytes, CodecMode.READ_ONLY));
	}

	/**
	 * Wipes the contents of this {@code PropertyContainer}.
	 * It will be completely empty after this method is invoked.
	 */
	public void clear() {
		booleanProps.clear();
		byteProps.clear();
		floatProps.clear();
		intProps.clear();
		stringProps.clear();
	}

	/**
	 * Retrieves the {@code boolean} associated with the specified key, or returns {@code false} if no such {@code boolean} exists.
	 * @param key The key to lookup the {@code boolean} with. Case insensitive.
	 * @return The {@code boolean} corresponding to the key, or {@code false} otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public boolean getBoolean(String key) {
		return booleanProps.contains(key.toUpperCase(Locale.ENGLISH));
	}

	/**
	 * Associates the given {@code boolean} with the specified key in this {@code PropertyContainer}.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public void putBoolean(String key, boolean value) {
		key = key.toUpperCase(Locale.ENGLISH);
		if (value)
			booleanProps.add(key);
		else
			booleanProps.remove(key);
	}

	/**
	 * Retrieves the {@code byte} array associated with the specified key, or returns {@code null} if no such {@code byte} array exists.
	 * @param key The key to lookup the {@code byte} array with. Case insensitive.
	 * @return The {@code byte} array corresponding to the key, or {@code null} otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	@Nullable
	public byte[] getByteArray(String key) {
		return byteProps.get(key.toUpperCase(Locale.ENGLISH)).value.clone();
	}

	/**
	 * Associates the given {@code byte} array with the specified key in this {@code PropertyContainer}.
	 * If the value is {@code null}, this method clears any prior association with the key.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value. A {@code null} value clears any previous association.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public void putByteArray(String key, @Nullable byte[] value) {
		putOrRemove(byteProps, key, value == null ? null : new ByteArray(value.clone()), null);
	}

	/**
	 * Retrieves the {@code float} associated with the specified key, or returns 0 if no such {@code float} exists.
	 * @param key The key to lookup the {@code float} with. Case insensitive.
	 * @return The {@code float} corresponding to the key, or 0 otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public float getFloat(String key) {
		return floatProps.getOrDefault(key.toUpperCase(Locale.ENGLISH), 0f);
	}

	/**
	 * Associates the given {@code float} with the specified key in this {@code PropertyContainer}.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public void putFloat(String key, float value) {
		putOrRemove(floatProps, key, value, 0f);
	}

	/**
	 * Retrieves the {@code int} associated with the specified key, or returns 0 if no such {@code int} exists.
	 * @param key The key to lookup the {@code int} with. Case insensitive.
	 * @return The {@code int} corresponding to the key, or 0 otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public int getInt(String key) {
		return intProps.getOrDefault(key.toUpperCase(Locale.ENGLISH), 0);
	}

	/**
	 * Associates the given {@code int} with the specified key in this {@code PropertyContainer}.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public void putInt(String key, int value) {
		putOrRemove(intProps, key, value, 0);
	}

	/**
	 * Retrieves the {@code String} associated with the specified key, or returns an empty string if no such {@code String} exists.
	 * @param key The key to lookup the {@code String} with. Case insensitive.
	 * @return The {@code String} corresponding to the key, or an empty one otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public String getString(String key) {
		return stringProps.getOrDefault(key.toUpperCase(Locale.ENGLISH), "");
	}

	/**
	 * Associates the given {@code String} with the specified key in this {@code PropertyContainer}.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value.
	 * @throws NullPointerException If {@code key} or {@code value} are {@code null}.
	 */
	public void putString(String key, String value) {
		putOrRemove(stringProps, key, Objects.requireNonNull(value, "value"), "");
	}

	/**
	 * Retrieves the value associated with the specified key, or returns {@code null} if no such value exists.
	 * @param key The key to lookup. Case insensitive.
	 * @return The value corresponding to the key, or {@code null} otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	@Nullable
	public Object get(String key) {
		key = key.toUpperCase(Locale.ENGLISH);

		if (booleanProps.contains(key)) return true;

		Object val = byteProps.get(key);
		if (val != null) {
			if (key.startsWith(NESTED_PROPERTY_CONTAINER_KEY))
				return getPropertyContainer(key);

			return val;
		}

		val = intProps.get(key);
		if (val != null) return val;

		val = floatProps.get(key);
		if (val != null) return val;

		val = stringProps.get(key);

		return val;
	}

	private static final String NESTED_PROPERTY_CONTAINER_KEY = "__NESTEDPB_";

	/**
	 * Retrieves the {@code PropertyContainer} associated with the specified key, or returns {@code null} if no such {@code PropertyContainer} exists.
	 * @param key The key to lookup the {@code PropertyContainer} with. Case insensitive.
	 * @return The {@code PropertyContainer} corresponding to the key, or {@code null} otherwise.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	@Nullable
	public PropertyContainer getPropertyContainer(String key) {
		final byte[] raw = getByteArray(NESTED_PROPERTY_CONTAINER_KEY + key);
		return raw == null ? null : new PropertyContainer(raw);
	}

	/**
	 * Associates the given {@code PropertyContainer} with the specified key in this {@code PropertyContainer}.
	 * If the value is {@code null}, this method clears any prior association with the key.
	 * @param key The key to associate the value with. Case insensitive.
	 * @param value The value. A {@code null} value clears any previous association.
	 * @throws NullPointerException If {@code key} is {@code null}.
	 */
	public void putPropertyContainer(String key, @Nullable PropertyContainer value) {
		key = NESTED_PROPERTY_CONTAINER_KEY + key;
		putByteArray(key, value == null ? null : value.write());
	}

	/**
	 * Writes this {@code PropertyContainer} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeListOf("BooleanProperties", booleanProps, SequenceEncoder::writeString);

		final List<ByteProperty> bps = new ArrayList<>(byteProps.size());
		for (var entry : byteProps.entrySet())
			bps.add(new ByteProperty(entry.getKey(), entry.getValue().value));
		to.writeObjectList("ByteProperties", bps, ByteProperty::write);

		final List<IntProperty> ips = new ArrayList<>(intProps.size());
		for (var entry : intProps.entrySet())
			ips.add(new IntProperty(entry.getKey(), entry.getValue()));
		to.writeObjectList("IntProperties", ips, IntProperty::write);

		final List<FloatProperty> fps = new ArrayList<>(floatProps.size());
		for (var entry : floatProps.entrySet())
			fps.add(new FloatProperty(entry.getKey(), entry.getValue()));
		to.writeObjectList("FloatProperties", fps, FloatProperty::write);

		final List<StringProperty> sps = new ArrayList<>(stringProps.size());
		for (var entry : stringProps.entrySet())
			sps.add(new StringProperty(entry.getKey(), entry.getValue()));
		to.writeObjectList("StringProperties", sps, StringProperty::write);
	}

	private byte[] write() {
		final BinaryCodec codec = new BinaryCodec(CodecMode.WRITE_ONLY);
		write(codec);
		return codec.toByteArray();
	}

	/**
	 * Flattens this {@code PropertyContainer} into a single {@link Map}, which is returned.
	 * @param sorted Whether the returned {@code Map} should be sorted.
	 * @return The {@code Map}.
	 */
	public Map<String, Object> flatten(boolean sorted) {
		final Map<String, Object> ret = sorted ? new TreeMap<>() : new LinkedHashMap<>();

		for (String key : booleanProps)
			ret.put(key, true);

		ret.putAll(floatProps);
		ret.putAll(intProps);
		ret.putAll(stringProps);

		for (var entry : byteProps.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();

			final ByteArray byteVal = entry.getValue();
			if (key.startsWith(NESTED_PROPERTY_CONTAINER_KEY)) {
				key = key.substring(NESTED_PROPERTY_CONTAINER_KEY.length());
				val = new PropertyContainer(new BinaryCodec(byteVal.value, CodecMode.READ_ONLY)).flatten(sorted);
			} else if (byteVal.value.length == 16)
				val = new BinaryCodec(byteVal.value, CodecMode.READ_ONLY).readUUID();

			ret.put(key, val);
		}

		return ret;
	}

	private <T> void putOrRemove(Map<String, T> map, String key, T value, T def) {
		key = key.toUpperCase(Locale.ENGLISH);
		if (Objects.equals(value, def))
			map.remove(key);
		else
			map.put(checkKey(key), value);
	}

	private String checkKey(String key) {
		if (booleanProps.contains(key) || floatProps.containsKey(key)
				|| intProps.containsKey(key) || stringProps.containsKey(key)
				|| byteProps.containsKey(key) || byteProps.containsKey(NESTED_PROPERTY_CONTAINER_KEY + key))
			throw new IllegalArgumentException("Duplicate key " + key + ", currently associated with " + get(key));

		return key;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PropertyContainer pc)) return false;
		return booleanProps.equals(pc.booleanProps) && floatProps.equals(pc.floatProps)
				&& intProps.equals(pc.intProps) && stringProps.equals(pc.stringProps)
				&& byteProps.equals(pc.byteProps);
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleanProps, byteProps, floatProps, intProps, stringProps);
	}

	@Override
	public String toString() {
		return flatten(false).toString();
	}

	// A wrapper around byte[] for actual equals() and hashCode() impls because Java arrays don't have them.
	private static record ByteArray(byte[] value) {

		@Override
		public final boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof ByteArray ba)) return false;
			return Arrays.equals(value, ba.value);
		}

		@Override
		public final int hashCode() {
			return Arrays.hashCode(value);
		}
	}

	private static record ByteProperty(String key, byte[] value) {
		private ByteProperty {}

		private void write(Encoder to) {
			to.writeString("Key", key);

			if (to.writeCompressedTypes())
				to.writeByteArray("Value", value);

			// Fancier representation for non-binary formats:
			else if (key.startsWith(NESTED_PROPERTY_CONTAINER_KEY))
				to.writeObject("Value", new PropertyContainer(value), PropertyContainer::write);
			else if (value.length == 16)
				to.writeString("Value", new BinaryCodec(value, CodecMode.READ_ONLY).readUUID().toString());
			else
				to.writeByteArray("Value", value);
		}

		private static ByteProperty read(Decoder in) {
			final String key = in.readString("Key");
			byte[] value;

			if (in.readCompressedTypes())
				value = in.readByteArray("Value");

			// Fancier representation for non-binary formats:
			else if (key.startsWith(NESTED_PROPERTY_CONTAINER_KEY)) {
				final PropertyContainer pc = in.readObject("Value", PropertyContainer::new);
				value = pc.write();
			} else {
				// The following code is pretty bad, but there's no way to get the type before reading the value.
				// (At least without assuming implementations.)
				// We try to read a UUID, and if that fails we just try reading a byte array.
				// There *shouldn't* be any problems with positioning or anything since this code only runs in a non-sequence context.
				try {
					final UUID id = UUID.fromString(in.readString("Value"));
					final BinaryCodec codec = new BinaryCodec(CodecMode.WRITE_ONLY);
					codec.writeUUID(id);
					value = codec.toByteArray();
				} catch (Exception e) {
					value = in.readByteArray("Value");
				}
			}

			return new ByteProperty(key, value);
		}
	}

	private static record IntProperty(String key, int value) {
		private IntProperty {}
		private IntProperty(Decoder in) {
			this(in.readString("Key"), in.readInt("Value"));
		}

		private void write(Encoder to) {
			to.writeString("Key", key);
			to.writeInt("Value", value);
		}
	}

	private static record FloatProperty(String key, float value) {
		private FloatProperty {}
		private FloatProperty(Decoder in) {
			this(in.readString("Key"), in.readFloat("Value"));
		}

		private void write(Encoder to) {
			to.writeString("Key", key);
			to.writeFloat("Value", value);
		}
	}

	private static record StringProperty(String key, String value) {
		private StringProperty {}
		private StringProperty(Decoder in) {
			this(in.readString("Key"), in.readString("Value"));
		}

		private void write(Encoder to) {
			to.writeString("Key", key);
			to.writeString("Value", value);
		}
	}
}