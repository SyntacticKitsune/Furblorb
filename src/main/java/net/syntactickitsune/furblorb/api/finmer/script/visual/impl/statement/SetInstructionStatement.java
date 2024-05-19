package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Sets the default tooltip text displayed above the choice buttons, if no other tooltip is currently active."
 */
@RegisterSerializable("CommandSetInstruction")
public final class SetInstructionStatement extends StatementNode {

	/**
	 * The expression representing the default tooltip text.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code SetInstructionStatement} with default values.
	 */
	public SetInstructionStatement() {}

	/**
	 * Decodes a {@code SetInstructionStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SetInstructionStatement(Decoder in) {
		expression = new StringExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SetInstructionStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}