package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPlayerSetSpecies")
public final class PlayerSetSpeciesStatement extends StatementNode {

	public String singular;
	public String plural;
	public String coatNoun;
	public String coatAdjective;

	public PlayerSetSpeciesStatement() {}

	public PlayerSetSpeciesStatement(Decoder in) {
		singular = in.readString("Singular");
		plural = in.readString("Plural");
		coatNoun = in.readString("CoatNoun");
		coatAdjective = in.readString("CoatAdjective");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Singular", singular);
		to.writeString("Plural", plural);
		to.writeString("CoatNoun", coatNoun);
		to.writeString("CoatAdjective", coatAdjective);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetSpeciesStatement a)) return false;
		return Objects.equals(singular, a.singular) && Objects.equals(plural, a.plural)
				&& Objects.equals(coatNoun, a.coatNoun) && Objects.equals(coatAdjective, a.coatAdjective);
	}

	@Override
	public int hashCode() {
		return Objects.hash(singular, plural, coatNoun, coatAdjective);
	}
}