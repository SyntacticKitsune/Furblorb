package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "The number of hit points this Creature has left."
 */
@RegisterSerializable("ConditionPlayerHealth")
public final class PlayerHealthExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code PlayerHealthExpression} with default values.
	 */
	public PlayerHealthExpression() {}

	/**
	 * Decodes a {@code PlayerHealthExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerHealthExpression(Decoder in) {
		super(in);
	}
}