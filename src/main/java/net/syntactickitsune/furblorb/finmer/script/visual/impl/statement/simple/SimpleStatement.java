package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * A "simple" statement is a statement that takes no parameters and can therefore be a singleton.
 * This is the parent class of all such statements.
 * @author SyntacticKitsune
 */
public abstract class SimpleStatement extends StatementNode {

	@Override
	public final void write(Encoder to) {}

	@Override
	public final boolean equals(Object obj) {
		return this == obj || (obj != null && getClass() == obj.getClass());
	}

	@Override
	public final int hashCode() {
		return 0;
	}
}