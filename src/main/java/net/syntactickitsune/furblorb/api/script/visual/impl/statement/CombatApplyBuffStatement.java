package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.component.buff.Buff;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/combat">the documentation</a>:
 * "Applies a temporary effect to a participant (may be the Player or an NPC)."
 */
@RegisterSerializable("CommandCombatApplyBuff")
public final class CombatApplyBuffStatement extends StatementNode {

	/**
	 * The target of the statement.
	 */
	public Target target;

	/**
	 * For {@link Target#NPC}, determines the participant to apply the buff to.
	 */
	public String participantId = "";

	/**
	 * The effect to apply.
	 */
	public Buff effect;

	/**
	 * The number of turns to apply the buff for.
	 */
	public int duration; // Fun fact: the documentation on this field (in Finmer's source) is wrong.

	/**
	 * Constructs a new {@code CombatApplyBuffStatement} with default values.
	 */
	public CombatApplyBuffStatement() {}

	/**
	 * Decodes a {@code CombatApplyBuffStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatApplyBuffStatement(Decoder in) {
		target = in.readEnum("Target", Target.class);
		if (target == Target.NPC)
			participantId = in.readString("ParticipantID");
		effect = in.readOptional("Effect", FurballSerializables::read);
		duration = in.readInt("Duration");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Target", target);
		if (target == Target.NPC)
			to.writeString("ParticipantID", participantId);
		to.writeOptional("Effect", effect, Buff::writeWithId);
		to.writeInt("Duration", duration);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatApplyBuffStatement a)) return false;
		return target == a.target && Objects.equals(participantId, a.participantId)
				&& Objects.equals(effect, a.effect) && duration == a.duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(target, participantId, effect, duration);
	}

	/**
	 * The target of buff application. Either a player or NPC.
	 */
	public static enum Target implements INamedEnum {

		PLAYER("Player"),
		NPC("NPC");

		private final String id;

		private Target(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}