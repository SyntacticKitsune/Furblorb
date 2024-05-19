package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

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