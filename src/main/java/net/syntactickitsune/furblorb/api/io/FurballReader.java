package net.syntactickitsune.furblorb.api.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballDependency;
import net.syntactickitsune.furblorb.api.FurballMetadata;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.io.impl.BinaryCodec;
import net.syntactickitsune.furblorb.io.FurballSerializables;

/**
 * <p>
 * The {@code FurballReader} {@code class} reads {@link Furball Furballs} from {@link BinaryCodec BinaryCodecs}.
 * That is, it can transform a series of {@code byte}s into a {@code Furball}.
 * </p>
 * <p>
 * Using this class is as simple as:
 * <code><pre>
 * Furball furball = new FurballReader(buf).readFurball();</pre></code>
 * </p>
 * @author SyntacticKitsune
 * @see FurballWriter
 */
public final class FurballReader {

	static final byte[] MAGIC = { 'F', 'U', 'R', 'B', 'A', 'L', 'L' };

	private final BinaryCodec codec;

	/**
	 * Constructs a new {@code FurballReader} with the specified backing codec.
	 * @param codec The backing codec.
	 * @throws NullPointerException If {@code codec} is {@code null}.
	 */
	public FurballReader(BinaryCodec codec) {
		this.codec = Objects.requireNonNull(codec, "codec");
	}

	/**
	 * Constructs a new {@code FurballReader} with the specified backing buffer.
	 * @param buf The backing buffer.
	 * @throws NullPointerException If {@code buf} is {@code null}.
	 */
	public FurballReader(ByteBuffer buf) {
		this(new BinaryCodec(buf, true));
	}

	/**
	 * Constructs a new {@code FurballReader} with the specified backing array.
	 * This is a convenience constructor for using a wrapping little-endian {@link ByteBuffer}.
	 * @param bytes The backing array.
	 * @throws NullPointerException If {@code bytes} is {@code null}.
	 */
	public FurballReader(byte[] bytes) {
		this(ByteBuffer.wrap(Objects.requireNonNull(bytes, "bytes")));
		codec.buffer().order(ByteOrder.LITTLE_ENDIAN);
	}

	static void checkFormatVersion(byte formatVersion) {
		if (formatVersion < FurballMetadata.MINIMUM_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Furball too old: it has format version " + formatVersion + " but this parser only understands a minimum of " + FurballMetadata.MINIMUM_VERSION);
		if (formatVersion > FurballMetadata.LATEST_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Furball too new: it has format version " + formatVersion + " but this parser only understands a maximum of " + FurballMetadata.LATEST_VERSION);
	}

	/**
	 * Attempts to read the {@link FurballMetadata} from the {@code FurballReader}'s codec.
	 * The position of the codec after this method returns successfully will be at the end of the metadata block.
	 * Unless the codec is repositioned, it is generally inadvisable to {@linkplain #readFurball() read a furball} in this state,
	 * since it will attempt to re-read the metadata.
	 * @return The read metadata.
	 * @throws FurblorbParsingException If the data does not represent a furball or if some other parsing error occurs.
	 * @throws UnsupportedFormatVersionException If the furball described by the data has a format version that cannot be read by this {@code FurballReader}.
	 * @see #readFurball()
	 */
	public FurballMetadata readMetadata() throws FurblorbParsingException, UnsupportedFormatVersionException {
		// Check magic:
		for (int i = 0; i < MAGIC.length; i++) {
			final byte b = codec.readByte();
			if (b != MAGIC[i])
				throw new FurblorbParsingException("Not a furball: expected " + (char) MAGIC[i] + ", read " + (char) b);
		}

		final byte formatVersion = codec.readByte();

		checkFormatVersion(formatVersion);
		codec.setFormatVersion(formatVersion);

		return new FurballMetadata(codec, formatVersion);
	}

	/**
	 * Attempts to read a {@link Furball} from the {@code FurballReader}'s codec.
	 * It is important that the metadata of the furball is not read first, as that will cause the parsing to fail.
	 * @return The read furball.
	 * @throws FurblorbParsingException If the data does not represent a furball or if some other parsing error occurs.
	 * @throws UnsupportedFormatVersionException If the furball described by the data has a format version that cannot be read by this {@code FurballReader}.
	 */
	public Furball readFurball() throws FurblorbParsingException, UnsupportedFormatVersionException {
		final FurballMetadata meta = readMetadata();
		final Furball ret = new Furball(meta);

		ret.dependencies.addAll(codec.readList(FurballDependency::new));

		final int assetCount = codec.readInt();
		for (int i = 0; i < assetCount; i++) {
			FurballAsset asset;
			try {
				asset = FurballSerializables.read(codec);
			} catch (Exception e) {
				asset = null;
				System.err.println("At position " + codec.position() + ":");
				e.printStackTrace();

				if (assetCount > i)
					System.err.println("Skipping " + (assetCount - i) + " assets!");
				break;
			}

			ret.assets.add(asset);
		}

		return ret;
	}
}