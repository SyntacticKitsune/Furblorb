package net.syntactickitsune.furblorb.finmer.script.visual.impl.block;

import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Represents a loop that repeats for some number of times.
 * That is, {@code for i = 1, x do ... end}.
 */
// for (int i = 0; i < x - 1; i++)
// repeat with i running from 1 to x:
@RegisterSerializable("CommandLoopTimes")
public final class ForLoopStatement extends StatementBlockNode {

	/**
	 * The number of times the loop will repeat for.
	 */
	public IntExpression bound = new IntExpression(1);

	/**
	 * The contents of the loop.
	 */
	public List<ScriptNode> body;

	/**
	 * Constructs a new {@code LoopStatement} with default values.
	 */
	public ForLoopStatement() {}

	/**
	 * Decodes a {@code LoopStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ForLoopStatement(Decoder in) {
		bound = in.readObject("RepeatCount", FurballSerializables::read);
		body = read("LoopBody", in);
	}

	@Override
	public void write(Encoder to) {
		bound.writeWithId(to);
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
		if (!(obj instanceof ForLoopStatement a)) return false;
		return Objects.equals(bound, a.bound) && Objects.equals(body, a.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bound, body);
	}
}