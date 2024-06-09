package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "Grants the specified number of XP to the player, levelling them up if the requirements are met."
 */
@RegisterSerializable("CommandPlayerAddXP")
public final class PlayerAddXPStatement extends StatementNode {

	/**
	 * The amount of XP to add.
	 */
	public IntExpression expression;

	/**
	 * Constructs a new {@code PlayerAddXPStatement} with default values.
	 */
	public PlayerAddXPStatement() {}

	/**
	 * Decodes a {@code PlayerAddXPStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerAddXPStatement(Decoder in) {
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			expression.visit(visitor);
			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerAddXPStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}