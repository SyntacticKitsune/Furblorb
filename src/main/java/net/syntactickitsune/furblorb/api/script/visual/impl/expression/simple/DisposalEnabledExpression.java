package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "Whether the player has explicit disposal content enabled in the game settings."
 */
@RegisterSerializable("ConditionIsDisposalEnabled")
public final class DisposalEnabledExpression extends SimpleExpression {

	public DisposalEnabledExpression() {}

	public DisposalEnabledExpression(Decoder in) {}
}