package net.syntactickitsune.furblorb.api.io;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballMetadata;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.io.impl.BinaryCodec;

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
		this(new BinaryCodec(false));
	}

	static void checkFormatVersion(byte formatVersion) {
		if (formatVersion < FurballMetadata.MINIMUM_VERSION)
			throw new FurballFormatException(formatVersion, "Attempt to write a furball with a version older than min supported: " + formatVersion + " < " + FurballMetadata.MINIMUM_VERSION);
		if (formatVersion > FurballMetadata.LATEST_VERSION)
			throw new FurballFormatException(formatVersion, "Attempt to write a furball with a version newer than max supported: " + formatVersion + " > " + FurballMetadata.LATEST_VERSION);
	}

	// There is absolutely no reason I can see for people wanting to write *only* the metadata.
	private void writeMetadata(FurballMetadata meta) {
		Objects.requireNonNull(meta);

		// Write magic.
		for (int i = 0; i < FurballReader.MAGIC.length; i++)
			codec.writeByte((byte) FurballReader.MAGIC[i]);

		meta.write(codec);
	}

	/**
	 * Writes the specified {@code Furball} to the {@code FurballWriter}'s backing buffer.
	 * @param furball The furball to write.
	 * @return {@code this}.
	 * @throws FurballFormatException If the furball has a format version that cannot be written by this {@code FurballWriter}.
	 * @throws NullPointerException If {@code furball} is {@code null}.
	 */
	public FurballWriter write(Furball furball) throws FurballFormatException {
		Objects.requireNonNull(furball);

		checkFormatVersion(furball.meta.formatVersion);
		codec.setFormatVersion(furball.meta.formatVersion);

		writeMetadata(furball.meta);

		codec.writeList(furball.dependencies, (dep, enc) -> {
			enc.writeUUID("ID", dep.id());
			enc.writeString("FileNameHint", dep.filename());
		});

		codec.writeList(furball.assets, FurballAsset::writeWithId);

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