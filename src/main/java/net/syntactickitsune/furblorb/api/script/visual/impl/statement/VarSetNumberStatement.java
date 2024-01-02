package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandVarSetNumber")
public final class VarSetNumberStatement extends StatementNode {

	public String variable = "";
	public Operation op = Operation.SET;
	public FloatExpression expression; // Yo dawg, I heard ya like rounding errors, so I put some rounding errors in yer text-based game engine.

	public VarSetNumberStatement() {}

	public VarSetNumberStatement(Decoder in) {
		variable = in.readString("VariableName");
		op = in.readEnum("ValueOperation", Operation.class);

		if (op.binary)
			expression = new FloatExpression(in);
	}

	@Override
	public void write(Encoder to) {
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

	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Operation implements INamedEnum {

		// Java-style enums go brrr.
		ADD("Add", true),
		MULTIPLY("Multiply", true),
		DIVIDE("Divide", true),
		SET("Set", true),
		RANDOM("Random", true),
		SET_TIME_DAY("SetTimeDay", false),
		SET_TIME_HOUR("SetTimeHour", false),
		SET_TIME_HOUR_TOTAL("SetTimeHourTotal", false),
		SET_PLAYER_STRENGTH("SetPlayerStrength", false),
		SET_PLAYER_AGILITY("SetPlayerAgility", false),
		SET_PLAYER_BODY("SetPlayerBody", false),
		SET_PLAYER_WITS("SetPlayerWits", false),
		SET_PLAYER_MONEY("SetPlayerMoney", false),
		SET_PLAYER_LEVEL("SetPlayerLevel", false),
		SET_PLAYER_HEALTH("SetPlayerHealth", false),
		SET_PLAYER_HEALTH_MAX("SetPlayerHealthMax", false);

		private final String id;
		public final boolean binary;

		private Operation(String id, boolean binary) {
			this.id = id;
			this.binary = binary;
		}

		@Override
		public String id() {
			return id;
		}
	}
}