package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Whether or not there is any ongoing combat.
 */
@RegisterSerializable("ConditionCombatActive")
public final class CombatActiveExpression extends SimpleExpression {

	/**
	 * Constructs a new {@code CombatActiveExpression} with default values.
	 */
	public CombatActiveExpression() {}

	/**
	 * Decodes a {@code CombatActiveExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatActiveExpression(Decoder in) {}
}