package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;

/**
 * From <a href="https://docs.finmer.dev/script-reference/combat">the documentation</a>:
 * "Forces the two specified participants to enter into a grapple. The instigator will be 'on top', i.e. able to release or swallow the target."
 * Also allows the inverse: breaking grapples apart.
 */
@RegisterSerializable("CommandCombatSetGrappled")
public final class CombatSetGrappledStatement extends StatementNode {

	/**
	 * Whether to set or unset the grapple.
	 */
	public Mode mode;

	/**
	 * The name of the instigator.
	 */
	public String instigatorName;

	/**
	 * The name of the target.
	 */
	public String targetName;

	/**
	 * Constructs a new {@code CombatSetGrappledStatement} with default values.
	 */
	public CombatSetGrappledStatement() {}

	/**
	 * Decodes a {@code CombatSetGrappledStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatSetGrappledStatement(Decoder in) {
		mode = in.readEnum("Mode", Mode.class);
		instigatorName = in.readString("InstigatorName");
		targetName = in.readString("TargetName");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Mode", mode);
		to.writeString("InstigatorName", instigatorName);
		to.writeString("TargetName", targetName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatSetGrappledStatement a)) return false;
		return mode == a.mode && Objects.equals(instigatorName, a.instigatorName)
				&& Objects.equals(targetName, a.targetName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, instigatorName, targetName);
	}

	/**
	 * Determines whether to set or unset a state.
	 */
	public static enum Mode implements INamedEnum {

		/**
		 * The state should be set.
		 */
		SET("Set"),

		/**
		 * The state should be unset.
		 */
		UNSET("Unset");

		private final String id;

		private Mode(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}