package net.syntactickitsune.furblorb.api.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.component.LoadOrderDependency;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.Script;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * A {@code ScriptAsset} contains the contents of a script, as well as any load order dependencies the script may have on other scripts.
 * See also <a href="https://docs.finmer.dev/asset-types/scripts">the documentation</a>.
 */
@RegisterSerializable("AssetScript")
public final class ScriptAsset extends FurballAsset {

	public Script contents;
	public final List<LoadOrderDependency> dependencies = new ArrayList<>();

	public ScriptAsset() {}

	public ScriptAsset(Decoder in) {
		super(in);

		contents = in.readOptional("Contents", FurballSerializables::read);

		dependencies.addAll(in.readList("LoadOrder", dec -> new LoadOrderDependency(
				dec.readUUID("TargetAsset"), dec.readEnum("Relation", LoadOrderDependency.Relation.class))));
	}

	@Override
	protected void write0(Encoder to) {
		to.writeOptional("Contents", contents, Script::writeWithId);

		to.writeList("LoadOrder", dependencies, (lod, enc) -> {
			enc.writeUUID("TargetAsset", lod.targetAsset());
			enc.writeEnum("Relation", lod.relation());
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ScriptAsset a)) return false;
		return id.equals(a.id) && Objects.equals(filename, a.filename)
				&& Objects.equals(dependencies, a.dependencies)
				&& Objects.equals(contents, a.contents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, filename, contents, dependencies);
	}
}