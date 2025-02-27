package net.syntactickitsune.furblorb.finmer.asset.scene.patch;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode.Properties;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;

@RegisterSerializable(value = "ScenePatchRemoveNode", since = 21)
public final class RemoveNodePatch extends ScenePatch {

	public String target = "";

	public RemoveNodePatch() {}

	public RemoveNodePatch(Decoder in) {
		target = in.readString("TargetNode");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("TargetNode", target);
	}

	@Override
	public Set<Properties> getAdditionalProperties() {
		return Set.of();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof RemoveNodePatch r)) return false;
		return Objects.equals(target, r.target);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(target);
	}
}