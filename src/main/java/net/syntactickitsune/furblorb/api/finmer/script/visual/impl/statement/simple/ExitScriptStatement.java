package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * A {@code return} statement.
 */
@RegisterSerializable("CommandExitScript")
public final class ExitScriptStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code ExitScriptStatement} with default values.
	 */
	public ExitScriptStatement() {}

	/**
	 * Decodes a {@code ExitScriptStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ExitScriptStatement(Decoder in) {}
}