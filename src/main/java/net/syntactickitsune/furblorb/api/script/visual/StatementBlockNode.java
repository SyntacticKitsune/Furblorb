package net.syntactickitsune.furblorb.api.script.visual;

import java.util.List;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.FurballSerializables;

public abstract class StatementBlockNode extends StatementNode {

	protected static List<ScriptNode> read(String key, Decoder in) {
		return in.readOptionalList(key, FurballSerializables::read);
	}

	protected static void write(String key, List<ScriptNode> list, Encoder out) {
		out.writeOptionalList(key, list, ScriptNode::writeWithId);
	}
}