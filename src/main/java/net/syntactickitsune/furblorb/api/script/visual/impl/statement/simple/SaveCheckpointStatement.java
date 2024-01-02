package net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandSaveCheckpoint")
public final class SaveCheckpointStatement extends SimpleStatement {

	public SaveCheckpointStatement() {}

	public SaveCheckpointStatement(Decoder in) {}
}