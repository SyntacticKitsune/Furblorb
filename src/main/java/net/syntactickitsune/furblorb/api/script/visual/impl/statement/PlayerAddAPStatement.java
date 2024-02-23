package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Gives the player some number of additional ability points (AP).
 */
@RegisterSerializable("CommandPlayerAddAP")
public final class PlayerAddAPStatement extends StatementNode {

	/**
	 * The number of ability points to add.
	 */
	public IntExpression expression;

	/**
	 * Constructs a new {@code PlayerAddAPStatement} with default values.
	 */
	public PlayerAddAPStatement() {}

	/**
	 * Decodes a {@code PlayerAddAPStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerAddAPStatement(Decoder in) {
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerAddAPStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}