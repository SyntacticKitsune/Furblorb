package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Ends the current game with a 'game-over' screen. The user cannot interact with the scene anymore, and will be able to exit the game or reload save data."
 */
@RegisterSerializable("CommandEndGame")
public final class EndGameStatement extends SimpleStatement {

	public EndGameStatement() {}

	public EndGameStatement(Decoder in) {}
}