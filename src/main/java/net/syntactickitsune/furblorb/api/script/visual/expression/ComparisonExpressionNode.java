package net.syntactickitsune.furblorb.api.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;

public abstract class ComparisonExpressionNode extends ExpressionNode {

	public Operator op;
	public FloatExpression target; // I'm gonna be honest, floats here seem like a terrible idea.

	protected ComparisonExpressionNode() {}

	protected ComparisonExpressionNode(Decoder in) {
		op = in.readEnum("Operator", Operator.class);
		target = new FloatExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Operator", op);
		target.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final ComparisonExpressionNode cen = (ComparisonExpressionNode) obj;
		return op == cen.op && Objects.equals(target, cen.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(op, target);
	}

	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Operator implements INamedEnum {

		EQUALS("Equal"),
		NOT_EQUALS("NotEqual"),
		LESS_THAN("Lesser"),
		LESS_THAN_OR_EQUAL_TO("LesserOrEqual"),
		GREATER_THAN("Greater"),
		GREATER_THAN_OR_EQUAL_TO("GreaterOrEqual");

		private final String id;

		private Operator(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}