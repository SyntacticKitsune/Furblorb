package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Restores the player's health back up to their max health.
 */
@RegisterSerializable("CommandPlayerHealAll")
public final class PlayerHealAllStatement extends SimpleStatement {

	public PlayerHealAllStatement() {}

	public PlayerHealAllStatement(Decoder in) {}
}