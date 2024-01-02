package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable(value = "ConditionTimeIsNight", since = 20)
public final class TimeIsNightExpression extends SimpleExpression {

	public TimeIsNightExpression() {}

	public TimeIsNightExpression(Decoder in) {}
}