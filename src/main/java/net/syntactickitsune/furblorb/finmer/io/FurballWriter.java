package net.syntactickitsune.furblorb.finmer.io;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.FurballDependency;
import net.syntactickitsune.furblorb.finmer.FurballMetadata;
import net.syntactickitsune.furblorb.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;

/**
 * <p>
 * The {@code FurballWriter} {@code class} writes {@link Furball Furballs} to {@link BinaryCodec BinaryCodecs}.
 * That is, it can transform a {@code Furball} into a series of {@code byte}s.
 * </p>
 * <p>
 * Using this class is as simple as:
 * <code><pre>
 * byte[] bytes = new FurballWriter().write(furball).toByteArray();</pre></code>
 * </p>
 * @author SyntacticKitsune
 * @see FurballReader
 */
public final class FurballWriter {

	private final BinaryCodec codec;

	/**
	 * Constructs a new {@code FurballWriter} with the specified backing codec.
	 * @param codec The backing codec.
	 */
	public FurballWriter(BinaryCodec codec) {
		this.codec = Objects.requireNonNull(codec, "codec");
	}

	/**
	 * Constructs a new {@code FurballWriter} with an empty backing codec.
	 */
	public FurballWriter() {
		this(new BinaryCodec(CodecMode.WRITE_ONLY));
	}

	/**
	 * Checks to make sure the specified format version is supported by Furblorb's API.
	 * @param formatVersion The format version to check.
	 * @throws UnsupportedFormatVersionException If the format version is unsupported.
	 */
	static void checkFormatVersion(byte formatVersion) {
		if (formatVersion < FurballMetadata.MINIMUM_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Attempt to write a furball with a version older than min supported: " + formatVersion + " < " + FurballMetadata.MINIMUM_VERSION);
		if (formatVersion > FurballMetadata.LATEST_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Attempt to write a furball with a version newer than max supported: " + formatVersion + " > " + FurballMetadata.LATEST_VERSION);
	}

	/**
	 * Writes the specified {@code Furball} to the {@code FurballWriter}'s backing buffer.
	 * @param furball The furball to write.
	 * @return {@code this}.
	 * @throws UnsupportedFormatVersionException If the furball has a format version that cannot be written by this {@code FurballWriter}.
	 * @throws NullPointerException If {@code furball} is {@code null}.
	 */
	public FurballWriter write(Furball furball) throws UnsupportedFormatVersionException {
		Objects.requireNonNull(furball);
		Objects.requireNonNull(furball.meta);

		checkFormatVersion(furball.meta.formatVersion);
		codec.setFormatVersion(furball.meta.formatVersion);

		codec.writeBytes(FurballReader.MAGIC);

		codec.writeByte(furball.meta.formatVersion);

		// In format version 21, furballs are GZIP-compressed.
		// But we try to avoid allocating another codec if we can avoid it.
		final BinaryCodec compressedCodec;
		if (furball.meta.formatVersion >= 21) {
			compressedCodec = new BinaryCodec(CodecMode.WRITE_ONLY);
			compressedCodec.setFormatVersion(codec.formatVersion());
			compressedCodec.setValidate(codec.validate());
		} else
			compressedCodec = codec;

		furball.meta.write(compressedCodec, false);
		compressedCodec.writeObjectList(furball.dependencies, FurballDependency::write);
		compressedCodec.writeObjectList(furball.assets, FurballAsset::writeWithId);

		if (furball.meta.formatVersion >= 21)
			codec.writeBytes(FurblorbUtil.compress(compressedCodec.toByteArray()));

		return this;
	}

	/**
	 * @return The backing {@code BinaryCodec}.
	 */
	public BinaryCodec codec() {
		return codec;
	}

	/**
	 * @return {@link #codec()}{@code .}{@link BinaryCodec#toByteArray() toByteArray()}.
	 */
	public byte[] toByteArray() {
		return codec.toByteArray();
	}
}