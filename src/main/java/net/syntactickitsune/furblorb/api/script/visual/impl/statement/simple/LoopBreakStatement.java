package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * A {@code break} statement.
 */
@RegisterSerializable("CommandLoopBreak")
public final class LoopBreakStatement extends SimpleStatement {

	public LoopBreakStatement() {}

	public LoopBreakStatement(Decoder in) {}
}