package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "Whether the player has the Explorer Mode feature enabled in the game settings."
 */
@RegisterSerializable("ConditionIsExplorerEnabled")
public final class ExplorerEnabledExpression extends SimpleExpression {

	public ExplorerEnabledExpression() {}

	public ExplorerEnabledExpression(Decoder in) {}
}