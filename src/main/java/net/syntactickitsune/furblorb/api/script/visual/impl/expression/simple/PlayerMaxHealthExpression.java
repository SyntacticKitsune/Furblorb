package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerHealthMax")
public final class PlayerMaxHealthExpression extends ComparisonExpressionNode {

	public PlayerMaxHealthExpression() {}

	public PlayerMaxHealthExpression(Decoder in) {
		super(in);
	}
}