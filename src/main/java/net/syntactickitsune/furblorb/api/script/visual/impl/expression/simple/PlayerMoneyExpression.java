package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerMoney")
public final class PlayerMoneyExpression extends ComparisonExpressionNode {

	public PlayerMoneyExpression() {}

	public PlayerMoneyExpression(Decoder in) {
		super(in);
	}
}