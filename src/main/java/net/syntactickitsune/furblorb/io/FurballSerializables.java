package net.syntactickitsune.furblorb.io;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.FurblorbUtil;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * <p>
 * A reimplementation of Finmer's {@code AssetSerializer} class, or at least, the reflective part of it.
 * </p>
 * <p>
 * {@code FurballSerializables} tracks all of the {@link IFurballSerializable} classes that are annotated
 * with {@link RegisterSerializable} and manages the (de-)serialization of these classes.
 * </p>
 * @author SyntacticKitsune
 */
public final class FurballSerializables {

	private static final Map<Integer, Metadata> SERIALIZABLES_BY_ID = new LinkedHashMap<>();
	private static final Map<String, Metadata> SERIALIZABLES_BY_TYPE = new LinkedHashMap<>();
	private static final Map<Class, Metadata> SERIALIZABLES_BY_CLASS = new IdentityHashMap<>();

	static {
		final List<String> targets = FurblorbUtil.readStringResource("/serializables.txt");
		// Time for some extremely exciting reflection.
		for (String target : targets)
			try {
				final Class<?> cls = Class.forName(target);
				final RegisterSerializable rs = Objects.requireNonNull(cls.getAnnotation(RegisterSerializable.class), () -> target + " is missing a RegisterSerializable annotation!");
				final int id = FurblorbUtil.hash(rs.value());

				// Ready for the spicy part?
				final java.lang.reflect.Constructor<?> reflectCtor = cls.getDeclaredConstructor(Decoder.class);
				final MethodHandle handle = MethodHandles.publicLookup().unreflectConstructor(reflectCtor);

				final Constructor ctor = dec -> {
					try {
						return (IFurballSerializable) handle.invoke(dec);
					} catch (Throwable e) {
						return FurblorbUtil.throwAsUnchecked(e);
					}
				};

				register(id, rs.value(), (Class) cls, ctor);
			} catch (Exception e) {
				throw new IllegalStateException("Failed to register " + target, e);
			}
	}

	private static <T extends IFurballSerializable> void register(int id, String name, Class<T> clazz, Constructor<T> ctor) {
		if (SERIALIZABLES_BY_ID.containsKey(id))
			throw new IllegalArgumentException("Cannot register " + clazz.getName() + " under id " + id + ", as it is already owned by " + SERIALIZABLES_BY_ID.get(id).owner.getName());

		final Metadata meta = new Metadata<>(id, name, ctor, clazz);

		SERIALIZABLES_BY_ID.put(id, meta);
		SERIALIZABLES_BY_TYPE.put(name, meta);
		SERIALIZABLES_BY_CLASS.put(clazz, meta);
	}

	/**
	 * Lookup an {@link IFurballSerializable}'s metadata by its ID (hash of its C# class name).
	 * @param id The ID of the desired {@code IFurballSerializable}.
	 * @return The {@code IFurballSerializable}'s metadata.
	 */
	@Internal
	@Nullable
	public static Metadata lookupById(int id) {
		return SERIALIZABLES_BY_ID.get(id);
	}

	/**
	 * Lookup an {@link IFurballSerializable}'s metadata by its type (C# class name).
	 * @param type The type of the desired {@code IFurballSerializable}.
	 * @return The {@code IFurballSerializable}'s metadata.
	 */
	@Internal
	@Nullable
	public static Metadata lookupByType(String type) {
		return SERIALIZABLES_BY_TYPE.get(type);
	}

	/**
	 * Lookup an {@link IFurballSerializable}'s metadata by its class.
	 * @param clazz The class of the {@code IFurballSerializable}.
	 * @return The {@code IFurballSerializable}'s metadata.
	 */
	@Internal
	@Nullable
	public static Metadata lookupByClass(Class<? extends IFurballSerializable> clazz) {
		return SERIALIZABLES_BY_CLASS.get(clazz);
	}

	/**
	 * @return An unmodifiable view of all registered {@link IFurballSerializable} metadata.
	 */
	@Internal
	public static Collection<Metadata> lookupAll() {
		return Collections.unmodifiableCollection(SERIALIZABLES_BY_CLASS.values());
	}

	/**
	 * Attempts to read the next {@link IFurballSerializable} from the given {@code Decoder}.
	 * @param <T> The exact type of the desired {@code IFurballSerializable}.
	 * @param in The {@code Decoder} to read from.
	 * @return The read {@code IFurballSerializable}.
	 * @throws UnknownSerializableException If the next ID or type read from the {@code Decoder} does not represent a known {@code IFurballSerializable} implementation.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public static <T extends IFurballSerializable> T read(Decoder in) {
		final Metadata md;

		if (in.readCompressedTypes()) {
			final int id = in.readInt("!Type");
			md = lookupById(id);
			if (md == null)
				throw new UnknownSerializableException(id);
		} else {
			final String type = in.readString("!Type");
			md = lookupByType(type);
			if (md == null)
				throw new UnknownSerializableException(type);
		}

		return (T) md.ctor._new(in);
	}

	/**
	 * Contains all of the metadata that {@link FurballSerializables} tracks about each {@link IFurballSerializable}.
	 *
	 * @author SyntacticKitsune
	 *
	 * @param <T> The type of {@code IFurballSerializable}.
	 * @param id The ID of the {@code IFurballSerializable}. This is the hash of its {@code name}.
	 * @param name The C# class name of the {@code IFurballSerializable}. This is discovered from {@link RegisterSerializable}.
	 * @param ctor The {@code IFurballSerializable}'s one-parameter {@link Decoder} constructor.
	 * @param owner The {@code IFurballSerializable} class.
	 */
	@Internal
	public static record Metadata<T extends IFurballSerializable>(int id, String name, Constructor<T> ctor, Class<T> owner) {}

	/**
	 * Represents an {@link IFurballSerializable}'s one-parameter {@link Decoder} constructor.
	 *
	 * @author SyntacticKitsune
	 *
	 * @param <T> The type of {@code IFurballSerializable}.
	 */
	@Internal
	@FunctionalInterface
	public static interface Constructor<T extends IFurballSerializable> {

		/**
		 * Invokes the one-parameter {@code Decoder} constructor represented by this {@code Constructor}.
		 * @param in The {@code Decoder} to invoke the constructor with.
		 * @return The new {@code IFurballSerializable} instance.
		 */
		public T _new(Decoder in);
	}
}