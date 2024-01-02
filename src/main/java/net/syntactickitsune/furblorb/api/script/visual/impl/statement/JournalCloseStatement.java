package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandJournalClose")
public final class JournalCloseStatement extends StatementNode {

	public UUID journalId;

	public JournalCloseStatement() {}

	public JournalCloseStatement(Decoder in) {
		journalId = in.readUUID("JournalGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("JournalGuid", journalId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof JournalCloseStatement a)) return false;
		return Objects.equals(journalId, a.journalId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(journalId);
	}
}