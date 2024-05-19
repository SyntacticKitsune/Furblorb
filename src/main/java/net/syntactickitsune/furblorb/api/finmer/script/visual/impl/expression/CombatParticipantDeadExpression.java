package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/creature">the documentation</a>:
 * "Returns whether this Creature currently has 0 HP."
 */
@RegisterSerializable("ConditionCombatParDead")
public final class CombatParticipantDeadExpression extends ExpressionNode {

	/**
	 * The ID of the participant in question.
	 */
	public String participantId;

	/**
	 * Constructs a new {@code CombatParticipantDeadExpression} with default values.
	 */
	public CombatParticipantDeadExpression() {}

	/**
	 * Decodes a {@code CombatParticipantDeadExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatParticipantDeadExpression(Decoder in) {
		participantId = in.readString("ParticipantName");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("ParticipantName", participantId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatParticipantDeadExpression a)) return false;
		return Objects.equals(participantId, a.participantId);
	}

	@Override
	public int hashCode() {
		return participantId.hashCode();
	}
}