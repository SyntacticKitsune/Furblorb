package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/logging">the documentation</a>:
 * "Erases all text in the game log."
 */
@RegisterSerializable("CommandLogClear")
public final class LogClearStatement extends SimpleStatement {

	public LogClearStatement() {}

	public LogClearStatement(Decoder in) {}
}