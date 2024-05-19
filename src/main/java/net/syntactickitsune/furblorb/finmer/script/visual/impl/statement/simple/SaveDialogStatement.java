package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/save">the documentation</a>:
 * "Opens a screen where the player can choose to save their progress to disk."
 */
@RegisterSerializable("CommandSaveDialog")
public final class SaveDialogStatement extends SimpleStatement {

	/**
	 * Constructs a new {@code SaveDialogStatement} with default values.
	 */
	public SaveDialogStatement() {}

	/**
	 * Decodes a {@code SaveDialogStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SaveDialogStatement(Decoder in) {}
}