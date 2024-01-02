package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandJournalUpdate")
public final class JournalUpdateStatement extends StatementNode {

	public UUID journalId;
	public int stage;

	public JournalUpdateStatement() {}

	public JournalUpdateStatement(Decoder in) {
		journalId = in.readUUID("JournalGuid");
		stage = in.readInt("Stage");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("JournalGuid", journalId);
		to.writeInt("Stage", stage);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof JournalUpdateStatement a)) return false;
		return stage == a.stage && Objects.equals(journalId, a.journalId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(journalId, stage);
	}
}