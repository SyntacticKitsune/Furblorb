package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandLogClear")
public final class LogClearStatement extends SimpleStatement {

	public LogClearStatement() {}

	public LogClearStatement(Decoder in) {}
}