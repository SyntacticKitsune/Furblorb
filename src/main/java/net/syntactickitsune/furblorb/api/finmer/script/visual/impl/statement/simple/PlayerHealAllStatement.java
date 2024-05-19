package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Restores the player's health back up to their max health.
 */
@RegisterSerializable("CommandPlayerHealAll")
public final class PlayerHealAllStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code PlayerHealAllStatement} with default values.
	 */
	public PlayerHealAllStatement() {}

	/**
	 * Decodes a {@code PlayerHealAllStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerHealAllStatement(Decoder in) {}
}