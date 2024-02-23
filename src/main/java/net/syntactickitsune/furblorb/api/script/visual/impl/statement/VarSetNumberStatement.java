package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.lang.reflect.Field;
import java.util.Objects;

import net.syntactickitsune.furblorb.FurblorbUtil;
import net.syntactickitsune.furblorb.api.RequiresFormatVersion;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.IllegalFormatVersionException;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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
		ADD("Add", true),
		MULTIPLY("Multiply", true),
		DIVIDE("Divide", true),
		SET("Set", true),
		@RequiresFormatVersion(20)
		RANDOM("Random", true),
		@RequiresFormatVersion(20)
		SET_TIME_DAY("SetTimeDay", false),
		@RequiresFormatVersion(20)
		SET_TIME_HOUR("SetTimeHour", false),
		@RequiresFormatVersion(20)
		SET_TIME_HOUR_TOTAL("SetTimeHourTotal", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_STRENGTH("SetPlayerStrength", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_AGILITY("SetPlayerAgility", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_BODY("SetPlayerBody", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_WITS("SetPlayerWits", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_MONEY("SetPlayerMoney", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_LEVEL("SetPlayerLevel", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_HEALTH("SetPlayerHealth", false),
		@RequiresFormatVersion(20)
		SET_PLAYER_HEALTH_MAX("SetPlayerHealthMax", false);

		private final String id;
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