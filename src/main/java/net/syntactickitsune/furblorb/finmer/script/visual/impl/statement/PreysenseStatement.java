package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

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

	/**
	 * Constructs a new {@code PreysenseStatement} with default values.
	 */
	public PreysenseStatement() {}

	/**
	 * Decodes a {@code PreysenseStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PreysenseStatement(Decoder in) {
		mode = in.readCompressedInt("Mode");
		creatureId = in.readUUID("CreatureGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeCompressedInt("Mode", mode);
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