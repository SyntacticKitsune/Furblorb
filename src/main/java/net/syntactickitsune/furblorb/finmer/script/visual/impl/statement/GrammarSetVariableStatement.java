package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/grammar">the documentation</a>:
 * "Registers a replacement variable. After this function is called, any text in string tables in the form of <code>{!name}</code> will be replaced with {@code value}."
 */
@RegisterSerializable("CommandGrammarSetVariable")
public final class GrammarSetVariableStatement extends StatementNode {

	/**
	 * The variable.
	 */
	public String variable;

	/**
	 * The replacement expression.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code GrammarSetVariableStatement} with default values.
	 */
	public GrammarSetVariableStatement() {}

	/**
	 * Decodes a {@code GrammarSetVariableStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public GrammarSetVariableStatement(Decoder in) {
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
		if (!(obj instanceof GrammarSetVariableStatement a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, expression);
	}
}