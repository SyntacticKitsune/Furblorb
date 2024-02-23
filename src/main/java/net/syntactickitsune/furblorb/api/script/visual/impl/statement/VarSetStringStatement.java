package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/storage">the documentation</a>:
 * "Saves a string with the specified key to persistent storage."
 */
@RegisterSerializable("CommandVarSetString")
public final class VarSetStringStatement extends StatementNode {

	/**
	 * The key to associate the string with.
	 */
	public String variable;

	/**
	 * The string to save.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code VarSetStringStatement} with default values.
	 */
	public VarSetStringStatement() {}

	/**
	 * Decodes a {@code VarSetStringStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarSetStringStatement(Decoder in) {
		variable = in.readString("VariableName");
		expression = new StringExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarSetStringStatement a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, expression);
	}
}