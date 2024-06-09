package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Sets the player's current name.
 */
@RegisterSerializable("CommandPlayerSetName")
public final class PlayerSetNameStatement extends StatementNode {

	/**
	 * The value to change the player's name to.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code PlayerSetNameStatement} with default values.
	 */
	public PlayerSetNameStatement() {}

	/**
	 * Decodes a {@code PlayerSetNameStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerSetNameStatement(Decoder in) {
		expression = new StringExpression(in);
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
		if (!(obj instanceof PlayerSetNameStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}