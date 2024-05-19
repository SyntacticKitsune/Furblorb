package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/logging">the documentation</a>:
 * "Adds a horizontal bar (splitter) to the game log." In other words, like a <code>&lt;hr&gt;</code> (horizontal rule) in HTML.
 */
@RegisterSerializable("CommandLogSplit")
public final class LogSplitStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code LogSplitStatement} with default values.
	 */
	public LogSplitStatement() {}

	/**
	 * Decodes a {@code LogSplitStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LogSplitStatement(Decoder in) {}
}