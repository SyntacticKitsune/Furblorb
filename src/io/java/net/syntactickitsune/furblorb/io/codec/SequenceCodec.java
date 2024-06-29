package net.syntactickitsune.furblorb.io.codec;

import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;

/**
 * The {@code SequenceCodec} class serves as the base class for unifying {@link SequenceDecoder} and {@link SequenceEncoder}.
 * This is the sequence-based specialization of {@link Codec}.
 * @author SyntacticKitsune
 * @see DelegatingSequenceCodec
 */
public abstract class SequenceCodec extends Codec implements SequenceDecoder, SequenceEncoder {

	/**
	 * Constructs a new {@code SequenceCodec}.
	 * @param mode The mode of the {@code SequenceCodec}.
	 * @throws NullPointerException If {@code mode} is {@code null}.
	 */
	protected SequenceCodec(CodecMode mode) {
		super(mode);
	}

	@Override
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException {}
}