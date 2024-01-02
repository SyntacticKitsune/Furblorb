package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable(value = "ConditionTimeHour", since = 20)
public final class TimeHourExpression extends ComparisonExpressionNode {

	public TimeHourExpression() {}

	public TimeHourExpression(Decoder in) {
		super(in);
	}
}