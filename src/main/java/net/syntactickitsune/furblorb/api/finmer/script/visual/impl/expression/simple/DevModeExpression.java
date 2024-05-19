package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Checks whether Developer Mode is enabled. Dev Mode is enabled by clicking the Launch Dev Mode option in the Editor, or passing the '-dev' command line option to the game."
 */
@RegisterSerializable("ConditionIsDevModeEnabled")
public final class DevModeExpression extends SimpleExpression {

	/**
	 * Constructs a new {@code DevModeExpression} with default values.
	 */
	public DevModeExpression() {}

	/**
	 * Decodes a {@code DevModeExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public DevModeExpression(Decoder in) {}
}