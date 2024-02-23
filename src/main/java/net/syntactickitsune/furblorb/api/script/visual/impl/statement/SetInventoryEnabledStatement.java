package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	public SetInventoryEnabledStatement() {}

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