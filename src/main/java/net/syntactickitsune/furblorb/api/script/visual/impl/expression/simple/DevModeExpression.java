package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Checks whether Developer Mode is enabled. Dev Mode is enabled by clicking the Launch Dev Mode option in the Editor, or passing the '-dev' command line option to the game."
 */
@RegisterSerializable("ConditionIsDevModeEnabled")
public final class DevModeExpression extends SimpleExpression {

	public DevModeExpression() {}

	public DevModeExpression(Decoder in) {}
}