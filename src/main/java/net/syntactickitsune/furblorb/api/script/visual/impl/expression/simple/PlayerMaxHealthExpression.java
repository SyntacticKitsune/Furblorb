package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "The max number of hit points this Creature can have. This is the sum of the Body stat and applicable equipment effects."
 */
@RegisterSerializable("ConditionPlayerHealthMax")
public final class PlayerMaxHealthExpression extends ComparisonExpressionNode {

	public PlayerMaxHealthExpression() {}

	public PlayerMaxHealthExpression(Decoder in) {
		super(in);
	}
}