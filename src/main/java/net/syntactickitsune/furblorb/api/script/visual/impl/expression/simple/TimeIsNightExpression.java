package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * <p>
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Returns whether the current world clock time is either earlier than 06:00, or later than 20:00."
 * </p>
 * <p>
 * This expression is only available from format version 20 (Finmer v1.0.1) onwards.
 * </p>
 */
@RegisterSerializable(value = "ConditionTimeIsNight", since = 20)
public final class TimeIsNightExpression extends SimpleExpression {

	public TimeIsNightExpression() {}

	public TimeIsNightExpression(Decoder in) {}
}