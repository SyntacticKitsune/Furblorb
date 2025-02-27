package net.syntactickitsune.furblorb.finmer.asset.scene.patch;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode.Properties;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;

@RegisterSerializable(value = "PatchTypeReplaceNode", since = 21)
public final class ReplaceNodePatch extends ScenePatch {

	public String target = "";
	public boolean keepChildren;

	public ReplaceNodePatch() {}

	public ReplaceNodePatch(Decoder in) {
		target = in.readString("TargetNode");
		keepChildren = in.readBoolean("KeepChildren");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("TargetNode", target);
		to.writeBoolean("KeepChildren", keepChildren);
	}

	@Override
	public Set<Properties> getAdditionalProperties() {
		return EnumSet.of(Properties.SCRIPTS, Properties.CHILDREN);
	}
}