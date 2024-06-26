package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.PlayerSetHealthStatement.Strategy;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Modifies the player's current money.
 */
@RegisterSerializable("CommandPlayerSetMoney")
public final class PlayerSetMoneyStatement extends StatementNode {

	/**
	 * Determines whether to add or set the value.
	 */
	public Strategy strategy;

	/**
	 * The value to add or set.
	 */
	public IntExpression expression;

	/**
	 * Constructs a new {@code PlayerSetMoneyStatement} with default values.
	 */
	public PlayerSetMoneyStatement() {}

	/**
	 * Decodes a {@code PlayerSetMoneyStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerSetMoneyStatement(Decoder in) {
		strategy = in.readEnum("ValueOperation", Strategy.class);
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("ValueOperation", strategy);
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
		if (!(obj instanceof PlayerSetMoneyStatement a)) return false;
		return strategy == a.strategy && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(strategy, expression);
	}
}