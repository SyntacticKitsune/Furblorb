package net.syntactickitsune.furblorb.api.script;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Represents a series of statements built using Finmer's visual scripting framework.
 * Kinda like a {@link Runnable} -- does whatever given zero parameters and yields no result.
 */
@RegisterSerializable("ScriptDataVisualAction")
public final class VisualActionScript extends Script { // Microsoft two seconds after Flash gets popular.

	/**
	 * The list of statements to execute.
	 */
	public final List<ScriptNode> nodes = new ArrayList<>();

	public VisualActionScript() {}

	public VisualActionScript(Decoder in) {
		nodes.addAll(in.readOptionalList("Nodes", FurballSerializables::read));
	}

	@Override
	public void write(Encoder to) {
		to.writeOptionalList("Nodes", nodes, ScriptNode::writeWithId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VisualActionScript a)) return false;
		return nodes.equals(a.nodes);
	}

	@Override
	public int hashCode() {
		return nodes.hashCode();
	}
}