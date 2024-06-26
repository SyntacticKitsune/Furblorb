package net.syntactickitsune.furblorb.io.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbException;
import net.syntactickitsune.furblorb.io.ParsingStrategy;

/**
 * The {@code Codec} class serves as the base class for unifying {@link Encoder} and {@link Decoder}.
 * Since the supported formats are very different from each other, this class does not do much more
 * than implement the two interfaces and handle the format version tracking (something which is
 * universal between all {@code Codecs}).
 * @author SyntacticKitsune
 */
public abstract class Codec implements Encoder, Decoder {

	private static final Map<Class<?>, ParsingStrategy.NumberType> ENUM_NUMBER_CACHE = new HashMap<>();

	/**
	 * A special value indicating an unset format version.
	 */
	protected static final byte UNSET_FORMAT_VERSION = 0;

	/**
	 * The format version of the {@code Codec}.
	 * @see #formatVersion()
	 * @see #setFormatVersion(byte)
	 */
	protected byte formatVersion = UNSET_FORMAT_VERSION;

	/**
	 * See {@link #validate()}.
	 */
	protected boolean validate = true;

	/**
	 * The mode of the {@code Codec}.
	 */
	protected final CodecMode mode;

	/**
	 * Constructs a new {@code Codec}.
	 * @param mode The mode of the {@code Codec}.
	 * @throws NullPointerException If {@code mode} is {@code null}.
	 */
	protected Codec(CodecMode mode) {
		Objects.requireNonNull(mode, "mode");
		this.mode = mode;
	}

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

	@Override
	public boolean validate() {
		return validate;
	}

	/**
	 * Changes {@linkplain #validate() validate} to the specified value.
	 * @param value The new value.
	 */
	public void setValidate(boolean value) {
		validate = value;
	}

	/**
	 * <p>
	 * Determines the {@linkplain net.syntactickitsune.furblorb.io.ParsingStrategy.NumberType number type} of the specified enum class.
	 * The number type specifies the size of the numbers that the enum constants should be written as.
	 * </p>
	 * <p>
	 * If the enum class lacks a {@link ParsingStrategy} annotation, then a best guess based on the number of enum constants is used,
	 * preferring the smallest number type that can still encode the largest {@linkplain Enum#ordinal() enum constant ordinal}.
	 * </p>
	 * @param clazz The class to identify the number type of.
	 * @return The corresponding number type.
	 * @see ParsingStrategy
	 */
	protected final ParsingStrategy.NumberType numberType(Class<? extends Enum> clazz) {
		return ENUM_NUMBER_CACHE.computeIfAbsent(clazz, k -> {
			@Nullable
			final ParsingStrategy ps = k.getAnnotation(ParsingStrategy.class);
			if (ps != null) return ps.value();

			final int count = clazz.getEnumConstants().length;
			if (count <= 256) return ParsingStrategy.NumberType.BYTE;
			if (count <= 65536) return ParsingStrategy.NumberType.SHORT;

			return ParsingStrategy.NumberType.INT;
		});
	}
}