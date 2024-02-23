package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/journal">the documentation</a>:
 * "Sets the specified quest to the specified stage number (as preconfigured in the Journal asset), and updates the journal list."
 */
@RegisterSerializable("CommandJournalUpdate")
public final class JournalUpdateStatement extends StatementNode {

	/**
	 * The ID of the journal entry to update.
	 */
	public UUID journalId;

	/**
	 * The new stage value.
	 */
	public int stage;

	/**
	 * Constructs a new {@code JournalUpdateStatement} with default values.
	 */
	public JournalUpdateStatement() {}

	/**
	 * Decodes a {@code JournalUpdateStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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