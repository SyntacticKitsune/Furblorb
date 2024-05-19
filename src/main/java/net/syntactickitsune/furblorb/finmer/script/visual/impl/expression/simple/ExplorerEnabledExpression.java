package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "Whether the player has the Explorer Mode feature enabled in the game settings."
 */
@RegisterSerializable("ConditionIsExplorerEnabled")
public final class ExplorerEnabledExpression extends SimpleExpression {

	/**
	 * Constructs a new {@code ExplorerEnabledExpression} with default values.
	 */
	public ExplorerEnabledExpression() {}

	/**
	 * Decodes a {@code ExplorerEnabledExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ExplorerEnabledExpression(Decoder in) {}
}