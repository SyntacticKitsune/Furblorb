package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	public VarFlagExpression() {}

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