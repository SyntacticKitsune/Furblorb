package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/logging">the documentation</a>:
 * "If the user has the preysense feature enabled, displays a customizable content warning."
 */
@RegisterSerializable("CommandPreysense")
public final class PreysenseStatement extends StatementNode {

	/**
	 * The {@linkplain SenseType mode} of this {@code PreysenseStatement}.
	 */
	public int mode;

	/**
	 * The ID of the creature this {@code PreysenseStatement} applies to.
	 */
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

	/**
	 * Checks whether the specified {@code SenseType} is set on this {@code PreysenseStatement}.
	 * @param type The {@code SenseType} to check.
	 * @return {@code true} if the specified {@code SenseType} is set.
	 */
	public boolean isSet(SenseType type) {
		return (mode & type.flag) != 0;
	}

	/**
	 * Adds or removes the specified {@code SenseType} from this {@code PreysenseStatement}, as specified by {@code value}.
	 * @param type The {@code SenseType} in question.
	 * @param value Whether to add ({@code true}) or remove ({@code false}) the {@code SenseType}.
	 */
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

	/**
	 * Represents each of the available "sense types."
	 */
	public static enum SenseType {

		/**
		 * Oral vore is involved.
		 */
		ORAL_VORE,

		/**
		 * Anal vore is involved.
		 */
		ANAL_VORE,

		/**
		 * Cock vore is involved.
		 */
		COCK_VORE,

		/**
		 * Unbirth is involved.
		 */
		UNBIRTH,

		/**
		 * The vore is endo, i.e. the player is perfectly fine.
		 */
		ENDO,

		/**
		 * The vore may be endo or fatal, depending on a condition (like how long you stick around).
		 */
		ENDO_OR_FATAL,

		/**
		 * The vore is fatal, but the player is reformed afterward.
		 */
		DIGESTION_REFORM,

		/**
		 * The vore is fatal, and leads to a game-over.
		 */
		DIGESTION_FATAL;

		/**
		 * The flag corresponding to this {@code SenseType}.
		 */
		public final short flag = (short) (1 << ordinal());
	}
}