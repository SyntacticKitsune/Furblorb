package net.syntactickitsune.furblorb.finmer.io;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;
import net.syntactickitsune.furblorb.io.codec.SequenceCodec;

/**
 * <p>
 * {@code FurballCodec} is a specialized {@link SequenceCodec} designed to be used for Finmer's furball format.
 * While {@link BinaryCodec} is normally suitable for this, {@code FurballCodec} also takes care of differences
 * between different format versions, such as many {@code int}s being compressed in format version 21 and up.
 * </p>
 * <p>
 * {@code FurballCodec} is not intended to be used directly.
 * Instead, one should use {@link FurballReader} or {@link FurballWriter}, which both handle the other
 * intricacies of the furball file format.
 * </p>
 * @author SyntacticKitsune
 * @see FurballReader
 * @see FurballWriter
 */
public class FurballCodec extends BinaryCodec {

	protected final BinaryCodec delegate;

	/**
	 * Constructs a new {@code FurballCodec} from the specified codec.
	 * @param codec The delegate codec to use for read/write operations.
	 */
	public FurballCodec(BinaryCodec codec) {
		super(codec.buffer(), CodecMode.READ_AND_WRITE);
		delegate = codec;
	}

	@Override
	public byte[] toByteArray() {
		return delegate.toByteArray();
	}

	@Override
	public void checkRead() {
		delegate.checkRead();
	}

	@Override
	public void checkWrite(int length) {
		delegate.checkWrite(length);
		buf = delegate.buffer();
	}

	@Override
	public byte formatVersion() {
		return delegate.formatVersion();
	}

	@Override
	public void setFormatVersion(byte value) {
		delegate.setFormatVersion(value);
	}

	@Override
	public boolean validate() {
		return delegate.validate();
	}

	@Override
	public void setValidate(boolean value) {
		delegate.setValidate(value);
	}

	@Override
	public void assertDoesNotExist(String key, String message) throws FurblorbParsingException {
		delegate.assertDoesNotExist(key, message);
	}

	@Override
	protected int readLength() {
		return readCompressedInt(null);
	}

	@Override
	protected void writeLength(int length) {
		writeCompressedInt(null, length);
	}

	@Override
	public int readCompressedInt(@Nullable String key) {
		return formatVersion() >= 21 ? super.readCompressedInt(key) : readInt(key);
	}

	@Override
	public void writeCompressedInt(@Nullable String key, int value) {
		if (formatVersion() >= 21)
			super.writeCompressedInt(key, value);
		else
			writeInt(key, value);
	}

	@Override
	public byte @Nullable [] readOptionalByteArray() {
		final int len = readCompressedInt(null);
		if (len <= 0) return null;
		return readBytes(len);
	}

	@Override
	public void writeOptionalByteArray(byte @Nullable [] value) {
		if (formatVersion() < 21) {
			super.writeOptionalByteArray(value);
			return;
		}

		if (value == null)
			writeCompressedInt(null, 0);
		else
			writeByteArray(value);
	}
}