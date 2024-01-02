package net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionIsExplorerEnabled")
public final class ExplorerEnabledExpression extends SimpleExpression {

	public ExplorerEnabledExpression() {}

	public ExplorerEnabledExpression(Decoder in) {}
}