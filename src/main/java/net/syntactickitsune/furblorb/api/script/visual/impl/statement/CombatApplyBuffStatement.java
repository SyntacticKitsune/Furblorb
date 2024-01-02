package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.component.buff.Buff;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandCombatApplyBuff")
public final class CombatApplyBuffStatement extends StatementNode {

	public Target target;
	public String participantId = "";
	public Buff effect;
	public int duration; // Fun fact: the documentation on this field is wrong.

	public CombatApplyBuffStatement() {}

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