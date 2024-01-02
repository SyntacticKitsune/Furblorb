package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionVarFlag")
public final class VarFlagExpression extends ExpressionNode {

	public String variable = "";
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