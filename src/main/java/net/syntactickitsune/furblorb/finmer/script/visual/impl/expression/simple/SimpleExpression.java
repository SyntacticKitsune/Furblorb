package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * A "simple" expression is an expression that takes no parameters and can therefore be a singleton.
 * This is the parent class of all such expressions.
 * @author SyntacticKitsune
 */
public abstract class SimpleExpression extends ExpressionNode {

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		return getClass() == obj.getClass();
	}

	@Override
	public final int hashCode() {
		return 0;
	}

	@Override
	public final void write(Encoder to) {}
}