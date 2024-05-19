package net.syntactickitsune.furblorb.api.finmer.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.component.LoadOrderDependency;
import net.syntactickitsune.furblorb.api.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.Script;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * A {@code ScriptAsset} contains the contents of a script, as well as any load order dependencies the script may have on other scripts.
 * See also <a href="https://docs.finmer.dev/asset-types/scripts">the documentation</a>.
 */
@RegisterSerializable("AssetScript")
public final class ScriptAsset extends FurballAsset {

	/**
	 * The contents of the script.
	 */
	public Script contents;

	/**
	 * A list of dependencies the script has on other scripts.
	 */
	public final List<LoadOrderDependency> dependencies = new ArrayList<>();

	/**
	 * Constructs a new {@code ScriptAsset} with default values.
	 */
	public ScriptAsset() {}

	/**
	 * Decodes a {@code ScriptAsset} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ScriptAsset(Decoder in) {
		super(in);

		contents = in.readOptional("Contents", FurballSerializables::read);
		dependencies.addAll(in.readList("LoadOrder", LoadOrderDependency::new));
	}

	@Override
	protected void write0(Encoder to) {
		to.writeOptional("Contents", contents, Script::writeWithId);
		to.writeList("LoadOrder", dependencies, LoadOrderDependency::write);
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