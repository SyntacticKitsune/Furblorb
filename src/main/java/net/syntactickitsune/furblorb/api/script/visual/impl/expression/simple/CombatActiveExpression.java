package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Whether or not there is any ongoing combat.
 */
@RegisterSerializable("ConditionCombatActive")
public final class CombatActiveExpression extends SimpleExpression {

	public CombatActiveExpression() {}

	public CombatActiveExpression(Decoder in) {}
}