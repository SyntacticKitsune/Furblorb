package net.syntactickitsune.furblorb.finmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.finmer.io.FurballReader;
import net.syntactickitsune.furblorb.finmer.io.FurballWriter;

/**
 * <p>
 * Represents Finmer's "furball" file format.
 * </p>
 * <p>
 * Furballs are essentially Finmer's compiled project file format.
 * Each furball has a set of {@linkplain FurballMetadata metadata} that identifies itself,
 * a list of other furballs it depends on, and a series of assets belonging to it.
 * Furballs are normally exported by Finmer's editor, but this can also be performed by Furblorb.
 * </p>
 * <p>
 * Furballs can be read using {@link FurballReader} and written using {@link FurballWriter}.
 * They can also be "decompiled" into Finmer projects using {@link FinmerProjectWriter}, and
 * projects can be "compiled" into furballs using {@link FinmerProjectReader}.
 * </p>
 * @see FurballReader
 * @see FurballWriter
 * @see FinmerProjectReader
 * @see FinmerProjectWriter
 */
public final class Furball {

	/**
	 * The furball's metadata.
	 */
	public final FurballMetadata meta;

	/**
	 * The list containing the furball's dependencies.
	 */
	public final List<FurballDependency> dependencies = new ArrayList<>();

	/**
	 * The list containing the furball's assets.
	 */
	public final List<FurballAsset> assets = new ArrayList<>();

	/**
	 * Constructs a new {@code Furball}.
	 * @param meta The metadata describing this {@code Furball}.
	 * @throws NullPointerException If {@code meta} is {@code null}.
	 */
	public Furball(FurballMetadata meta) {
		this.meta = Objects.requireNonNull(meta, "meta");
	}

	@Override
	// Extremely ambitious task: comparing furballs.
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Furball f)) return false;
		return meta.equals(f.meta) && dependencies.equals(f.dependencies) && assets.equals(f.assets);
	}

	@Override
	public int hashCode() {
		return Objects.hash(meta, dependencies, assets);
	}
}