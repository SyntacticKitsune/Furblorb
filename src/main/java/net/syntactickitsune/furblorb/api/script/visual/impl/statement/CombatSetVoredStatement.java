package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.impl.statement.CombatSetGrappledStatement.Mode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandCombatSetVored")
public final class CombatSetVoredStatement extends StatementNode {

	public Mode mode;
	public String predatorName;
	public String preyName;

	public CombatSetVoredStatement() {}

	public CombatSetVoredStatement(Decoder in) {
		mode = in.readEnum("Mode", Mode.class);
		predatorName = in.readString("PredatorName");
		preyName = in.readString("PreyName");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Mode", mode);
		to.writeString("PredatorName", predatorName);
		to.writeString("PreyName", preyName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatSetVoredStatement a)) return false;
		return mode == a.mode && Objects.equals(predatorName, a.predatorName)
				&& Objects.equals(preyName, a.preyName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, predatorName, preyName);
	}
}