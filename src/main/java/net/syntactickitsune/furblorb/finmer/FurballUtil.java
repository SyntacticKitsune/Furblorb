package net.syntactickitsune.furblorb.finmer;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;

/**
 * Various {@link Furball}-related utilities.
 * @author SyntacticKitsune
 */
public final class FurballUtil {

	/**
	 * Represents an "empty" {@code UUID}, or at least, what Finmer uses to mean one.
	 */
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public static void checkInRange(String name, int value, int min, int max) {
		if (value < min || value > max)
			throw new FurblorbParsingException(name + " value out of range [" + min + ", " + max + "]: " + value);
	}

	public static <T> List<@Nullable T> readObjectList21(Decoder in, @Nullable String key, Function<Decoder, T> reader) {
		return in.formatVersion() >= 21 ? in.readObjectList(key, reader) : in.readOptionalObjectList(key, reader);
	}

	public static <T> void writeObjectList21(Encoder to, @Nullable String key, Collection<T> value, BiConsumer<T, Encoder> writer) {
		if (to.formatVersion() >= 21)
			to.writeObjectList(key, value, writer);
		else
			to.writeOptionalObjectList(key, value, writer);
	}

	@Nullable
	public static <T> T readObject21(Decoder in, @Nullable String key, Function<Decoder, T> reader) {
		return in.formatVersion() >= 21 ? in.readObject(key, reader) : in.readOptionalObject(key, reader);
	}

	public static <T> void writeObject21(Encoder to, @Nullable String key, T value, BiConsumer<T, Encoder> writer) {
		if (to.formatVersion() >= 21)
			to.writeObject(key, value, writer);
		else
			to.writeOptionalObject(key, value, writer);
	}
}