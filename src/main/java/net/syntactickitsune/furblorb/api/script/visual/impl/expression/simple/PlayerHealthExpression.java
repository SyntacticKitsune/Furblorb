package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerHealth")
public final class PlayerHealthExpression extends ComparisonExpressionNode {

	public PlayerHealthExpression() {}

	public PlayerHealthExpression(Decoder in) {
		super(in);
	}
}