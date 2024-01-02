package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable(value = "ConditionTimeHourTotal", since = 20)
public final class TimeHourTotalExpression extends ComparisonExpressionNode {

	public TimeHourTotalExpression() {}

	public TimeHourTotalExpression(Decoder in) {
		super(in);
	}
}