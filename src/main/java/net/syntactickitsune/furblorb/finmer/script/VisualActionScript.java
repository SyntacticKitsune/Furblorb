package net.syntactickitsune.furblorb.finmer.script;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

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

	/**
	 * Constructs a new empty {@code VisualActionScript}.
	 */
	public VisualActionScript() {}

	/**
	 * Decodes a {@code VisualActionScript} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 */
	public VisualActionScript(Decoder in) {
		nodes.addAll(in.readOptionalObjectList("Nodes", FurballSerializables::read));
	}

	@Override
	public void write(Encoder to) {
		to.writeOptionalObjectList("Nodes", nodes, ScriptNode::writeWithId);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this)) {
			for (ScriptNode sn : nodes)
				sn.visit(visitor);
			visitor.visitEnd();
		}
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