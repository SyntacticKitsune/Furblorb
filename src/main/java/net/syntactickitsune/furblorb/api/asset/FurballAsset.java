package net.syntactickitsune.furblorb.api.asset;

import java.util.UUID;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

/**
 * <p>
 * Represents an asset contained within a {@link Furball}.
 * Assets represent things like creatures, items, scripts, or scenes.
 * There is -- generally speaking -- a type of asset for every kind of file in a Finmer project.
 * </p>
 * <p>
 * At the time of writing, these are all the different kinds of assets:
 * <ul>
 * <li>{@link CreatureAsset}</li>
 * <li>{@link ItemAsset}</li>
 * <li>{@link JournalAsset}</li>
 * <li>{@link SceneAsset}</li>
 * <li>{@link ScriptAsset}</li>
 * <li>{@link StringTableAsset}</li>
 * </ul>
 * </p>
 * @author SyntacticKitsune
 */
public sealed abstract class FurballAsset implements IFurballSerializable, Comparable<FurballAsset> permits CreatureAsset, ItemAsset, JournalAsset, SceneAsset, ScriptAsset, StringTableAsset {

	public UUID id;
	public String filename;

	FurballAsset() {}

	FurballAsset(Decoder in) {
		id = in.readUUID("AssetID");
		filename = in.readString("AssetName");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("AssetID", id);
		to.writeString("AssetName", filename);
		write0(to);
	}

	protected abstract void write0(Encoder to);

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	@Override
	public int compareTo(FurballAsset o) {
		if (o == null) return 1;

		int tmp = filename.compareTo(o.filename);
		if (tmp != 0) return tmp;

		return id.compareTo(o.id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + filename + " (" + id + ")";
	}
}