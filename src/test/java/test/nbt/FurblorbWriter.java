package test.nbt;

import java.util.Objects;

import net.querz.nbt.tag.CompoundTag;

import net.syntactickitsune.furblorb.api.finmer.Furball;
import net.syntactickitsune.furblorb.api.finmer.FurballMetadata;
import net.syntactickitsune.furblorb.api.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.finmer.io.FurballWriter;
import net.syntactickitsune.furblorb.api.finmer.io.UnsupportedFormatVersionException;

/**
 * A {@link FurballWriter} for {@link NBTCodec}.
 * @author SyntacticKitsune
 */
public final class FurblorbWriter {

	private final NBTCodec codec;

	/**
	 * Constructs a new {@code FurblorbWriter} with the specified backing codec.
	 * @param codec The backing codec.
	 */
	public FurblorbWriter(NBTCodec codec) {
		this.codec = Objects.requireNonNull(codec, "codec");
	}

	/**
	 * Constructs a new {@code FurblorbWriter} with an empty backing codec.
	 */
	public FurblorbWriter() {
		this(new NBTCodec(new CompoundTag(), false));
	}

	static void checkFormatVersion(byte formatVersion) {
		if (formatVersion < FurballMetadata.MINIMUM_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Attempt to write a furball with a version older than min supported: " + formatVersion + " < " + FurballMetadata.MINIMUM_VERSION);
		if (formatVersion > FurballMetadata.LATEST_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Attempt to write a furball with a version newer than max supported: " + formatVersion + " > " + FurballMetadata.LATEST_VERSION);
	}

	// There is absolutely no reason I can see for people wanting to write *only* the metadata.
	private void writeMetadata(FurballMetadata meta) {
		Objects.requireNonNull(meta);

		codec.writeByte("FormatVersion", meta.formatVersion);
		codec.writeUUID("ID", Objects.requireNonNull(meta.id, "id"));
		codec.writeString("Title", Objects.requireNonNull(meta.title, "title"));
		codec.writeString("Author", Objects.requireNonNull(meta.author, "author"));
	}

	/**
	 * Writes the specified {@code Furball} to the {@code FurblorbWriter}'s backing buffer.
	 * @param furball The furball to write.
	 * @return {@code this}.
	 * @throws UnsupportedFormatVersionException If the furball has a format version that cannot be written by this {@code FurblorbWriter}.
	 * @throws NullPointerException If {@code furball} is {@code null}.
	 */
	public FurblorbWriter write(Furball furball) throws UnsupportedFormatVersionException {
		Objects.requireNonNull(furball);

		checkFormatVersion(furball.meta.formatVersion);
		codec.setFormatVersion(furball.meta.formatVersion);

		writeMetadata(furball.meta);

		codec.writeList("Dependencies", furball.dependencies, (dep, enc) -> {
			enc.writeUUID("ID", dep.id());
			enc.writeString("FileNameHint", dep.filename());
		});

		codec.writeList("Assets", furball.assets, FurballAsset::writeWithId);

		return this;
	}

	/**
	 * @return The backing {@code NBTCodec}.
	 */
	public NBTCodec codec() {
		return codec;
	}
}