package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/save">the documentation</a>:
 * "Create a checkpoint (auto-save). The player may load this checkpoint if their game ends."
 */
@RegisterSerializable("CommandSaveCheckpoint")
public final class SaveCheckpointStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code SaveCheckpointStatement} with default values.
	 */
	public SaveCheckpointStatement() {}

	/**
	 * Decodes a {@code SaveCheckpointStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SaveCheckpointStatement(Decoder in) {}
}