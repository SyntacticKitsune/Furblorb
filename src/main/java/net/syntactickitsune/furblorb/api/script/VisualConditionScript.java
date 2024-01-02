package net.syntactickitsune.furblorb.api.script;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.LogicalExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Represents part of Finmer's "visual" scripting pieces.
 */
@RegisterSerializable("ScriptDataVisualCondition")
public final class VisualConditionScript extends Script {

	public LogicalExpression expression;

	public VisualConditionScript() {}

	public VisualConditionScript(Decoder in) {
		expression = new LogicalExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VisualConditionScript a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
}