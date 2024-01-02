package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPreysense")
public final class PreysenseStatement extends StatementNode {

	public int mode;
	public UUID creatureId;

	public PreysenseStatement() {}

	public PreysenseStatement(Decoder in) {
		mode = in.readInt("Mode");
		creatureId = in.readUUID("CreatureGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeInt("Mode", mode);
		to.writeUUID("CreatureGuid", creatureId);
	}

	public boolean isSet(SenseType type) {
		return (mode & type.flag) != 0;
	}

	public void set(SenseType type, boolean value) {
		if (value)
			mode |= type.flag;
		else
			mode &= ~type.flag;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PreysenseStatement a)) return false;
		return mode == a.mode && Objects.equals(creatureId, a.creatureId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, creatureId);
	}

	public static enum SenseType {

		ORAL_VORE,
		ANAL_VORE,
		COCK_VORE,
		UNBIRTH,
		ENDO,
		ENDO_OR_FATAL,
		DIGESTION_REFORM,
		DIGESTION_FATAL;

		public final short flag = (short) (1 << ordinal());
	}
}