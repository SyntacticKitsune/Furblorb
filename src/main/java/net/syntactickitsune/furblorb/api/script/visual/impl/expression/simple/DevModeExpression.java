package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionIsDevModeEnabled")
public final class DevModeExpression extends SimpleExpression {

	public DevModeExpression() {}

	public DevModeExpression(Decoder in) {}
}