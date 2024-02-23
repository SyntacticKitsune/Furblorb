package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/logging">the documentation</a>:
 * "Adds a horizontal bar (splitter) to the game log." In other words, like a <code>&lt;hr&gt;</code> (horizontal rule) in HTML.
 */
@RegisterSerializable("CommandLogSplit")
public final class LogSplitStatement extends SimpleStatement {

	public LogSplitStatement() {}

	public LogSplitStatement(Decoder in) {}
}