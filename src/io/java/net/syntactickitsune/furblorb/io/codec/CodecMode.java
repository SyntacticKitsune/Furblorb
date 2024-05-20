package net.syntactickitsune.furblorb.io.codec;

/**
 * {@code CodecMode} represents the different modes for a {@link Codec} to be in.
 * @author SyntacticKitsune
 */
public enum CodecMode {

	/**
	 * The {@link Codec} is read-only; write operations will throw.
	 */
	READ_ONLY,

	/**
	 * The {@link Codec} is write-only; read operations will throw.
	 */
	WRITE_ONLY,

	/**
	 * The {@link Codec} can be both read from and written to.
	 */
	READ_AND_WRITE;

	/**
	 * Returns whether or not read operations are permitted under this mode.
	 * @return {@code true} if read operations are permitted.
	 */
	public boolean canRead() {
		return this != WRITE_ONLY;
	}

	/**
	 * Returns whether or not write operations are permitted under this mode.
	 * @return {@code true} if write operations are permitted.
	 */
	public boolean canWrite() {
		return this != READ_ONLY;
	}
}