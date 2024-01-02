package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionCombatParGrappling")
public final class CombatParticipantGrappledExpression extends ExpressionNode {

	public String participantId;
	public String targetName = "";

	public CombatParticipantGrappledExpression() {}

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