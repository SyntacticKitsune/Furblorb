package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.script.visual.impl.statement.PlayerSetStatStatement.Stat;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerStat")
public final class PlayerStatExpression extends ComparisonExpressionNode {

	public Stat stat;

	public PlayerStatExpression() {}

	public PlayerStatExpression(Decoder in) {
		super(in);
		stat = in.readEnum("Stat", Stat.class);
	}

	@Override
	public void write(Encoder to) {
		super.write(to);
		to.writeEnum("Stat", stat);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && stat == ((PlayerStatExpression) obj).stat;
	}

	@Override
	public int hashCode() {
		return Objects.hash(op, target, stat);
	}
}