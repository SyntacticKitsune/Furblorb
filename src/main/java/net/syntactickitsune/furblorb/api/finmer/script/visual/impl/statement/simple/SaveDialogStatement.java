package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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