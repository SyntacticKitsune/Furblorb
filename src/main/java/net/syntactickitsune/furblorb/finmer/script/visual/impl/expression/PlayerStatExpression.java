package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.PlayerSetStatStatement.Stat;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Checks the value of a stat and compares it to something.
 */
@RegisterSerializable("ConditionPlayerStat")
public final class PlayerStatExpression extends ComparisonExpressionNode {

	/**
	 * The stat to check.
	 */
	public Stat stat;

	/**
	 * Constructs a new {@code PlayerStatExpression} with default values.
	 */
	public PlayerStatExpression() {}

	/**
	 * Decodes a {@code PlayerStatExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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