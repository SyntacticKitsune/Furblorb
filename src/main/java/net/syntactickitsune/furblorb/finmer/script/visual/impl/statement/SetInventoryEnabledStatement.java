package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Sets whether or not the player can access their Character Sheet. If true, access is allowed, if false, the button will be greyed out."
 */
@RegisterSerializable("CommandSetInventoryEnabled")
public final class SetInventoryEnabledStatement extends StatementNode {

	/**
	 * The expression acting as the function parameter.
	 */
	public BooleanExpression expression;

	/**
	 * Constructs a new {@code SetInventoryEnabledStatement} with default values.
	 */
	public SetInventoryEnabledStatement() {}

	/**
	 * Decodes a {@code SetInventoryEnabledStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SetInventoryEnabledStatement(Decoder in) {
		expression = new BooleanExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SetInventoryEnabledStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}