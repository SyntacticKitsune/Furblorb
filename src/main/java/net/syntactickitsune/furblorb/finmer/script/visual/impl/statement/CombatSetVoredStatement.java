package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.impl.statement.CombatSetGrappledStatement.Mode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/combat">the documentation</a>:
 * "Forces the two specified participants to enter into a vore state, such that the prey is swallowed by the predator."
 * Also allows the inverse.
 */
@RegisterSerializable("CommandCombatSetVored")
public final class CombatSetVoredStatement extends StatementNode {

	/**
	 * Whether to set or unset the vore state.
	 */
	public Mode mode;

	/**
	 * The name of the predator.
	 */
	public String predatorName;

	/**
	 * The name of the prey.
	 */
	public String preyName;

	/**
	 * Constructs a new {@code CombatSetVoredStatement} with default values.
	 */
	public CombatSetVoredStatement() {}

	/**
	 * Decodes a {@code CombatSetVoredStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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