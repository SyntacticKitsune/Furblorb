package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/logging">the documentation</a>:
 * "Erases all text in the game log."
 */
@RegisterSerializable("CommandLogClear")
public final class LogClearStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code LogClearStatement} with default values.
	 */
	public LogClearStatement() {}

	/**
	 * Decodes a {@code LogClearStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LogClearStatement(Decoder in) {}
}