package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Checks whether a specified participant is grappling with anyone, and optionally, if they're grappling with a specific person.
 */
@RegisterSerializable("ConditionCombatParGrappling")
public final class CombatParticipantGrappledExpression extends ExpressionNode {

	/**
	 * The ID of the participant in question.
	 */
	public String participantId;

	/**
	 * If specified, the other person to check whether the participant is grappling with.
	 */
	public String targetName = "";

	/**
	 * Constructs a new {@code CombatParticipantGrappledExpression} with default values.
	 */
	public CombatParticipantGrappledExpression() {}

	/**
	 * Decodes a {@code CombatParticipantGrappledExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatParticipantGrappledExpression(Decoder in) {
		participantId = in.readString("ParticipantName");
		targetName = in.readString("TargetName");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("ParticipantName", participantId);
		to.writeString("TargetName", targetName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatParticipantGrappledExpression a)) return false;
		return Objects.equals(participantId, a.participantId) && Objects.equals(targetName, a.targetName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(participantId, targetName);
	}
}