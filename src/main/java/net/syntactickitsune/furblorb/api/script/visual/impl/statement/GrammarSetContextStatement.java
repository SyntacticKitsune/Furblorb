package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/grammar">the documentation</a>:
 * "Attaches the specified object to a context, so that if the specified name is later used in a grammar tag, this object will be used to resolve the grammar tag."
 */
@RegisterSerializable("CommandGrammarSetContext")
public final class GrammarSetContextStatement extends StatementNode {

	/**
	 * The name.
	 */
	public String variable;

	/**
	 * The object to associate with the name.
	 */
	public UUID creature;

	public GrammarSetContextStatement() {}

	public GrammarSetContextStatement(Decoder in) {
		variable = in.readString("VariableName");
		creature = in.readUUID("CreatureGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		to.writeUUID("CreatureGuid", creature);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof GrammarSetContextStatement a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(creature, a.creature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, creature);
	}
}