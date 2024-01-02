package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable(value = "ConditionTimeDay", since = 20)
public final class TimeDayExpression extends ComparisonExpressionNode {

	public TimeDayExpression() {}

	public TimeDayExpression(Decoder in) {
		super(in);
	}
}