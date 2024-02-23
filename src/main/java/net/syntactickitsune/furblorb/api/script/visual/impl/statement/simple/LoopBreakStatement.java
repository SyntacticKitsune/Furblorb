package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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