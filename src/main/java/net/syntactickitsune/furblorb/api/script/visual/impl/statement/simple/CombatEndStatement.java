package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/combat">the documentation</a>:
 * "Manually stops a combat session. Any pending XP will be awarded to the player, and the script that originally called {@code Begin} will be resumed."
 */
@RegisterSerializable("CommandCombatEnd")
public final class CombatEndStatement extends SimpleStatement {

	public CombatEndStatement() {}

	public CombatEndStatement(Decoder in) {}
}