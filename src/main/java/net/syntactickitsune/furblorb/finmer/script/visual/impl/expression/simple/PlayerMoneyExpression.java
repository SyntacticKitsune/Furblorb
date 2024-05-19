package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "The amount of money the player owns."
 */
@RegisterSerializable("ConditionPlayerMoney")
public final class PlayerMoneyExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code PlayerMoneyExpression} with default values.
	 */
	public PlayerMoneyExpression() {}

	/**
	 * Decodes a {@code PlayerMoneyExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerMoneyExpression(Decoder in) {
		super(in);
	}
}