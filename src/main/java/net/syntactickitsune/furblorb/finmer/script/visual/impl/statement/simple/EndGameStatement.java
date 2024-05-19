package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Ends the current game with a 'game-over' screen. The user cannot interact with the scene anymore, and will be able to exit the game or reload save data."
 */
@RegisterSerializable("CommandEndGame")
public final class EndGameStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code EndGameStatement} with default values.
	 */
	public EndGameStatement() {}

	/**
	 * Decodes a {@code EndGameStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public EndGameStatement(Decoder in) {}
}