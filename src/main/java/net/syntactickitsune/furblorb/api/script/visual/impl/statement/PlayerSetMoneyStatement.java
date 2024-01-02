package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.api.script.visual.impl.statement.PlayerSetHealthStatement.Strategy;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPlayerSetMoney")
public final class PlayerSetMoneyStatement extends StatementNode {

	public Strategy strategy;
	public IntExpression expression;

	public PlayerSetMoneyStatement() {}

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