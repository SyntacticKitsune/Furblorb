package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandCombatEnd")
public final class CombatEndStatement extends SimpleStatement {

	public CombatEndStatement() {}

	public CombatEndStatement(Decoder in) {}
}