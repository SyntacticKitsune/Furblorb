package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "The number of hit points this Creature has left."
 */
@RegisterSerializable("ConditionPlayerHealth")
public final class PlayerHealthExpression extends ComparisonExpressionNode {

	public PlayerHealthExpression() {}

	public PlayerHealthExpression(Decoder in) {
		super(in);
	}
}