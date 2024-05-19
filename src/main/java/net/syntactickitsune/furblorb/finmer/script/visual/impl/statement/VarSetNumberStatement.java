package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.lang.reflect.Field;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.io.IllegalFormatVersionException;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.ParsingStrategy;

/**
 * Represents a pretty generic statement that sets a variable to the number produced by your choice of 30 different things.
 * See {@link Operation} for details.
 */
@RegisterSerializable("CommandVarSetNumber")
public final class VarSetNumberStatement extends StatementNode {

	/**
	 * The name of the variable to store the result in.
	 */
	public String variable = "";

	/**
	 * The operation to perform.
	 */
	public Operation op = Operation.SET;

	/**
	 * For "binary" operations, the corresponding expression.
	 */
	public FloatExpression expression; // Yo dawg, I heard ya like rounding errors, so I put some rounding errors in yer text-based game engine.

	/**
	 * Constructs a new {@code VarSetNumberStatement} with default values.
	 */
	public VarSetNumberStatement() {}

	/**
	 * Decodes a {@code VarSetNumberStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarSetNumberStatement(Decoder in) {
		variable = in.readString("VariableName");
		op = in.readEnum("ValueOperation", Operation.class);

		if (op.binary)
			expression = new FloatExpression(in);
	}

	@Override
	public void write(Encoder to) {
		if (op.formatVersion > to.formatVersion())
			throw new IllegalFormatVersionException(op.formatVersion, to.formatVersion(), "serialize operation " + op.name());

		to.writeString("VariableName", variable);
		to.writeEnum("ValueOperation", op);

		if (op.binary) // The original code also writes it if the format is old enough, but that seems like a questionable choice.
			expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarSetNumberStatement a)) return false;
		return op == a.op && Objects.equals(variable, a.variable) && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, op, expression);
	}

	/**
	 * Represents the different operations that may be performed.
	 */
	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Operation implements INamedEnum {

		// Java-style enums go brrr.

		/**
		 * Add the specified number to the variable.
		 */
		ADD("Add", true),

		/**
		 * Multiply the variable by the specified number.
		 */
		MULTIPLY("Multiply", true),

		/**
		 * Divide the variable by the specified number.
		 */
		DIVIDE("Divide", true),

		/**
		 * Set the variable to the specified number.
		 */
		SET("Set", true),

		/**
		 * <p>
		 * Set the variable to a random number.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		RANDOM("Random", true),

		/**
		 * <p>
		 * Set the variable to the current day.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_TIME_DAY("SetTimeDay", false),

		/**
		 * <p>
		 * Set the variable to the current hour.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_TIME_HOUR("SetTimeHour", false),

		/**
		 * <p>
		 * Set the variable to the total number of hours passed.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_TIME_HOUR_TOTAL("SetTimeHourTotal", false),

		/**
		 * <p>
		 * Set the variable to the player's strength stat.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_STRENGTH("SetPlayerStrength", false),

		/**
		 * <p>
		 * Set the variable to the player's agility stat.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_AGILITY("SetPlayerAgility", false),

		/**
		 * <p>
		 * Set the variable to the player's body stat.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_BODY("SetPlayerBody", false),

		/**
		 * <p>
		 * Set the variable to the player's wits stat.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_WITS("SetPlayerWits", false),

		/**
		 * <p>
		 * Set the variable to the player's money count.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_MONEY("SetPlayerMoney", false),

		/**
		 * <p>
		 * Set the variable to the player's level.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_LEVEL("SetPlayerLevel", false),

		/**
		 * <p>
		 * Set the variable to the player's current health.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_HEALTH("SetPlayerHealth", false),

		/**
		 * <p>
		 * Set the variable to the player's maximum health.
		 * </p>
		 * <p>
		 * Only available from format version 20 (Finmer v1.0.1) onwards.
		 * </p>
		 */
		@RequiresFormatVersion(20)
		SET_PLAYER_HEALTH_MAX("SetPlayerHealthMax", false);

		private final String id;

		/**
		 * Whether the {@code Operation} has two parameters, rather than one.
		 */
		public final boolean binary;

		private byte formatVersion = 19;

		private Operation(String id, boolean binary) {
			this.id = id;
			this.binary = binary;
		}

		@Override
		public String id() {
			return id;
		}

		static {
			for (Field field : Operation.class.getDeclaredFields())
				if (field.isAnnotationPresent(RequiresFormatVersion.class))
					try {
						((Operation) field.get(null)).formatVersion = field.getAnnotation(RequiresFormatVersion.class).value();
					} catch (IllegalAccessException e) {
						FurblorbUtil.throwAsUnchecked(e);
					}
		}
	}
}