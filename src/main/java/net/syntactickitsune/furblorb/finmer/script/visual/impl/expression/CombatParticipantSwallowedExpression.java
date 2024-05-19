package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Checks whether a specified participant is swallowed, and optionally by who.
 */
@RegisterSerializable("ConditionCombatParSwallowed")
public final class CombatParticipantSwallowedExpression extends ExpressionNode {

	/**
	 * The ID of the participant in question.
	 */
	public String participantId;

	/**
	 * If specified, the name of the target predator of the interaction.
	 */
	public String predatorName = "";

	/**
	 * Constructs a new {@code CombatParticipantSwallowedExpression} with default values.
	 */
	public CombatParticipantSwallowedExpression() {}

	/**
	 * Decodes a {@code CombatParticipantSwallowedExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatParticipantSwallowedExpression(Decoder in) {
		participantId = in.readString("ParticipantName");
		predatorName = in.readString("PredatorName");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("ParticipantName", participantId);
		to.writeString("PredatorName", predatorName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatParticipantSwallowedExpression a)) return false;
		return Objects.equals(participantId, a.participantId) && Objects.equals(predatorName, a.predatorName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(participantId, predatorName);
	}
}