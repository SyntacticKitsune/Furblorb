package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/save">the documentation</a>:
 * "Create a checkpoint (auto-save). The player may load this checkpoint if their game ends."
 */
@RegisterSerializable("CommandSaveCheckpoint")
public final class SaveCheckpointStatement extends SimpleStatement {

	public SaveCheckpointStatement() {}

	public SaveCheckpointStatement(Decoder in) {}
}