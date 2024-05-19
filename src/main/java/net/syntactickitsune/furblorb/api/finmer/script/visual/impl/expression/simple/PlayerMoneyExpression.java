package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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