package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/storage">the documentation</a>:
 * "Saves a flag with the specified key to persistent storage."
 */
@RegisterSerializable("CommandVarSetFlag")
public final class VarSetFlagStatement extends StatementNode {

	/**
	 * The key to associate the flag with.
	 */
	public String variable;

	/**
	 * The flag to save.
	 */
	public BooleanExpression expression;

	/**
	 * Constructs a new {@code VarSetFlagStatement} with default values.
	 */
	public VarSetFlagStatement() {}

	/**
	 * Decodes a {@code VarSetFlagStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarSetFlagStatement(Decoder in) {
		variable = in.readString("VariableName");
		expression = new BooleanExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarSetFlagStatement a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, expression);
	}
}