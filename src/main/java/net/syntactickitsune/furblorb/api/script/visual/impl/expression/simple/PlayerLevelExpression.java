package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerLevel")
public final class PlayerLevelExpression extends ComparisonExpressionNode {

	public PlayerLevelExpression() {}

	public PlayerLevelExpression(Decoder in) {
		super(in);
	}
}