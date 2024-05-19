package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "The XP level of this Creature."
 */
@RegisterSerializable("ConditionPlayerLevel")
public final class PlayerLevelExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code PlayerLevelExpression} with default values.
	 */
	public PlayerLevelExpression() {}

	/**
	 * Decodes a {@code PlayerLevelExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerLevelExpression(Decoder in) {
		super(in);
	}
}