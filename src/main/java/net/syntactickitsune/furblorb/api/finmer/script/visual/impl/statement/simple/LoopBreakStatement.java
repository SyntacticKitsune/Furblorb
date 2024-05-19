package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * A {@code break} statement.
 */
@RegisterSerializable("CommandLoopBreak")
public final class LoopBreakStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code LoopBreakStatement} with default values.
	 */
	public LoopBreakStatement() {}

	/**
	 * Decodes a {@code LoopBreakStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LoopBreakStatement(Decoder in) {}
}