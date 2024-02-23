package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	public PlayerAddXPStatement() {}

	public PlayerAddXPStatement(Decoder in) {
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
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