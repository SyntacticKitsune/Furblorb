package net.syntactickitsune.furblorb.api.io.impl;

import java.util.HashMap;
import java.util.Map;

import net.syntactickitsune.furblorb.api.FurblorbException;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;

/**
 * The {@code Codec} class serves as the base class for unifying {@link Encoder} and {@link Decoder}.
 * Since the supported formats are very different from each other, this class does not do much more
 * than implement the two interfaces and handle the format version tracking (something which is
 * universal between all {@code Codecs}).
 * @author SyntacticKitsune
 */
public abstract class Codec implements Encoder, Decoder {

	private static final Map<Class<?>, ParsingStrategy.NumberType> ENUM_NUMBER_CACHE = new HashMap<>();

	private static final byte UNSET_FORMAT_VERSION = 0;

	protected byte formatVersion = UNSET_FORMAT_VERSION;

	@Override
	public byte formatVersion() {
		if (formatVersion == UNSET_FORMAT_VERSION) throw new FurblorbException("Format version is unset");
		return formatVersion;
	}

	/**
	 * <p>
	 * Changes the format version to the specified value.
	 * </p>
	 * <p>
	 * This method lives in {@link Codec} as opposed to {@link Encoder}
	 * or {@link Decoder} because it is not intended to be accessible from
	 * (de-)serializers. (De-)serialization code should not be able to change
	 * the format version while (de-)serializing, as that could cause unfathomable
	 * serialization errors. The owner of a particular {@code Codec} instance should
	 * decide the format version, and so this method lives here, as the owner will
	 * have a {@code Codec} (or ideally, an implementation), not a raw {@code Encoder}
	 * or {@code Decoder}.
	 * </p>
	 * @param value The new format version.
	 */
	public void setFormatVersion(byte value) {
		formatVersion = value;
	}

	protected final ParsingStrategy.NumberType numberType(Class<? extends Enum> clazz) {
		return ENUM_NUMBER_CACHE.computeIfAbsent(clazz, k -> {
			final ParsingStrategy ps = k.getAnnotation(ParsingStrategy.class);
			if (ps != null) return ps.value();

			final int count = clazz.getEnumConstants().length;
			if (count <= 256) return ParsingStrategy.NumberType.BYTE;
			if (count <= 65536) return ParsingStrategy.NumberType.SHORT;

			return ParsingStrategy.NumberType.INT;
		});
	}
}