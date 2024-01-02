package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandExitScript")
public final class ExitScriptStatement extends SimpleStatement {

	public ExitScriptStatement() {}

	public ExitScriptStatement(Decoder in) {}
}