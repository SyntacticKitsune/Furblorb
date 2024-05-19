package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/storage">the documentation</a>:
 * "Returns the state of a flag (boolean)."
 */
@RegisterSerializable("ConditionVarFlag")
public final class VarFlagExpression extends ExpressionNode {

	/**
	 * The name of the flag.
	 */
	public String variable = "";

	/**
	 * The expression to compare the flag to.
	 */
	public BooleanExpression target;

	/**
	 * Constructs a new {@code VarFlagExpression} with default values.
	 */
	public VarFlagExpression() {}

	/**
	 * Decodes a {@code VarFlagExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarFlagExpression(Decoder in) {
		variable = in.readString("VariableName");
		target = new BooleanExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		target.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarFlagExpression a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(target, a.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, target);
	}
}