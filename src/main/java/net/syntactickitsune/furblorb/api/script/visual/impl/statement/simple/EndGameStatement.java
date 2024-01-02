package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandEndGame")
public final class EndGameStatement extends SimpleStatement {

	public EndGameStatement() {}

	public EndGameStatement(Decoder in) {}
}