package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/save">the documentation</a>:
 * "Opens a screen where the player can choose to save their progress to disk."
 */
@RegisterSerializable("CommandSaveDialog")
public final class SaveDialogStatement extends SimpleStatement {

	public SaveDialogStatement() {}

	public SaveDialogStatement(Decoder in) {}
}