package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Sets the text displayed above the compass."
 */
@RegisterSerializable("CommandSetLocation")
public final class SetLocationStatement extends StatementNode {

	/**
	 * The expression representing the text to display.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code SetLocationStatement} with default values.
	 */
	public SetLocationStatement() {}

	/**
	 * Decodes a {@code SetLocationStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SetLocationStatement(Decoder in) {
		expression = new StringExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SetLocationStatement a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}