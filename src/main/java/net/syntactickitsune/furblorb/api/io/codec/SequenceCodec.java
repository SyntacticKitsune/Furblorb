package net.syntactickitsune.furblorb.api.io.codec;

import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.api.io.SequenceDecoder;
import net.syntactickitsune.furblorb.api.io.SequenceEncoder;

/**
 * The {@code SequenceCodec} class serves as the base class for unifying {@link SequenceDecoder} and {@link SequenceEncoder}.
 * This is the sequence-based specialization of {@link Codec}.
 * @author SyntacticKitsune
 */
public abstract class SequenceCodec extends Codec implements SequenceDecoder, SequenceEncoder {

	@Override
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException {}
}