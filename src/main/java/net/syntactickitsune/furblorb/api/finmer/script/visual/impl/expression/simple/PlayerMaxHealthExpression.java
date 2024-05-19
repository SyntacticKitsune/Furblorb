package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "The max number of hit points this Creature can have. This is the sum of the Body stat and applicable equipment effects."
 */
@RegisterSerializable("ConditionPlayerHealthMax")
public final class PlayerMaxHealthExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code PlayerMaxHealthExpression} with default values.
	 */
	public PlayerMaxHealthExpression() {}

	/**
	 * Decodes a {@code PlayerMaxHealthExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerMaxHealthExpression(Decoder in) {
		super(in);
	}
}