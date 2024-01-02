package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPlayerHealAll")
public final class PlayerHealAllStatement extends SimpleStatement {

	public PlayerHealAllStatement() {}

	public PlayerHealAllStatement(Decoder in) {}
}