package net.syntactickitsune.furblorb.finmer.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.FurballDependency;
import net.syntactickitsune.furblorb.finmer.FurballMetadata;
import net.syntactickitsune.furblorb.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;

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

	/**
	 * The furball "magic", i.e. the file data prefix that identifies the file as being a furball.
	 * See <a href="https://github.com/pileofwolves/finmer/blob/master/Finmer.Core/Serialization/FurballFileDeviceBinary.cs#L24">FurballFileDeviceBinary.cs</a>.
	 */
	static final byte[] MAGIC = { 'F', 'U', 'R', 'B', 'A', 'L', 'L' };

	private final BinaryCodec codec;
	private FurballCodec decompressedCodec;

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
		this(new BinaryCodec(buf, CodecMode.READ_ONLY));
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

	/**
	 * Checks to make sure the specified format version is supported by Furblorb's API.
	 * @param formatVersion The format version to check.
	 * @throws UnsupportedFormatVersionException If the format version is unsupported.
	 */
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

		// In format version 21, furballs are GZIP-compressed, so we may need to swap the codec.
		// (This code is also used in readFurball().)
		decompressedCodec = new FurballCodec(codec);
		if (formatVersion >= 21) {
			final byte[] bytes = codec.readBytes(codec.buffer().remaining());
			final BinaryCodec decompressedBinCodec = new BinaryCodec(FurblorbUtil.decompress(bytes), CodecMode.READ_ONLY);
			decompressedBinCodec.setFormatVersion(formatVersion);
			decompressedBinCodec.setValidate(codec.validate());
			decompressedCodec = new FurballCodec(decompressedBinCodec);
		}

		return new FurballMetadata(decompressedCodec, formatVersion);
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

		final int depCount = decompressedCodec.readInt(); // Note: not a 7-bit int!
		for (int i = 0; i < depCount; i++)
			ret.dependencies.add(decompressedCodec.readObject(FurballDependency::new));

		final int assetCount = decompressedCodec.readInt();
		for (int i = 0; i < assetCount; i++) {
			FurballAsset asset;
			try {
				asset = FurballSerializables.read(decompressedCodec);
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