package net.syntactickitsune.furblorb.api.finmer.script.visual;

import java.util.List;

import net.syntactickitsune.furblorb.api.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * <p>
 * The parent class of all visual scripting statement block nodes.
 * A block in this context meaning that it contains other statements as children.
 * (Think if-statements or loops.)
 * </p>
 * <p>
 * See the {@code visual.impl.block} package for all of the {@code StatementBlockNode} implementations.
 * </p>
 */
public abstract class StatementBlockNode extends StatementNode {

	/**
	 * Reads a list of {@link ScriptNode ScriptNodes} from the specified {@code Decoder} using the specified key.
	 * @param key The key that the nodes are associated with.
	 * @param in The {@code Decoder} to read from.
	 * @return The list of {@code ScriptNodes}.
	 */
	protected static List<ScriptNode> read(String key, Decoder in) {
		return in.readOptionalList(key, FurballSerializables::read);
	}

	/**
	 * Writes a list of {@link ScriptNode ScriptNodes} to the specified {@code Encoder} using the specified key.
	 * @param key The key that the nodes are associated with.
	 * @param list The list of nodes to write.
	 * @param out The {@code Encoder} to write to.
	 */
	protected static void write(String key, List<ScriptNode> list, Encoder out) {
		out.writeOptionalList(key, list, ScriptNode::writeWithId);
	}
}