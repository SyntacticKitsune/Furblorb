package net.syntactickitsune.furblorb.api.script.visual.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy.NumberType;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

public final class LogicalExpression implements IFurballSerializable {

	public final List<ExpressionNode> conditions = new ArrayList<>();
	public Mode mode = Mode.AND;
	public boolean target = true;

	public LogicalExpression() {}

	public LogicalExpression(Decoder in) {
		mode = in.readEnum("Mode", Mode.class);
		target = in.readBoolean("Operand");

		conditions.addAll(in.readOptionalList("Tests", FurballSerializables::read));
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Mode", mode);
		to.writeBoolean("Operand", target);

		to.writeOptionalList("Tests", conditions, ExpressionNode::writeWithId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LogicalExpression a)) return false;
		return mode == a.mode && target == a.target && Objects.equals(conditions, a.conditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(conditions, mode, target);
	}

	@ParsingStrategy(NumberType.INT)
	public static enum Mode implements INamedEnum {

		AND("All"),
		OR("Any");

		private final String id;

		private Mode(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}