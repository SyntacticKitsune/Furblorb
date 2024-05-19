package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/combat">the documentation</a>:
 * "Manually stops a combat session. Any pending XP will be awarded to the player, and the script that originally called {@code Begin} will be resumed."
 */
@RegisterSerializable("CommandCombatEnd")
public final class CombatEndStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code CombatEndStatement} with default values.
	 */
	public CombatEndStatement() {}

	/**
	 * Decodes a {@code CombatEndStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatEndStatement(Decoder in) {}
}