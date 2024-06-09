package net.syntactickitsune.furblorb.finmer.script.visual.impl.block;

import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

// Apparently this only represents the following loops:
//   while true do
//   for (;;) {
//   while eggs is eggs:
// (I suppose you just slap a break statement when you're done?)
//
// I'm going to be honest though, my favorite kind of loop probably has to be:
//   repeat with C running through unusual line comments:
/**
 * Represents a forever-repeating loop.
 * That is, {@code while true do ... end}.
 */
@RegisterSerializable("CommandLoop")
public final class LoopStatement extends StatementBlockNode {

	/**
	 * The contents of the loop.
	 */
	public List<ScriptNode> body;

	/**
	 * Constructs a new {@code LoopStatement} with default values.
	 */
	public LoopStatement() {}

	/**
	 * Decodes a {@code LoopStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LoopStatement(Decoder in) {
		body = read("LoopBody", in);
	}

	@Override
	public void write(Encoder to) {
		write("LoopBody", body, to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			for (ScriptNode sn : body)
				sn.visit(visitor);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LoopStatement a)) return false;
		return Objects.equals(body, a.body);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(body);
	}
}