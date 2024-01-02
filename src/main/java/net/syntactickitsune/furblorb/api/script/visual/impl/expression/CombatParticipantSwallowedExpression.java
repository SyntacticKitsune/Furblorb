package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionCombatParSwallowed")
public final class CombatParticipantSwallowedExpression extends ExpressionNode {

	public String participantId;
	public String predatorName = "";

	public CombatParticipantSwallowedExpression() {}

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