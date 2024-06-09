package net.syntactickitsune.furblorb.finmer.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.ParsingStrategy;

/**
 * Represents a {@code boolean} expression that compares a numeric value to another.
 */
public abstract class ComparisonExpressionNode extends ExpressionNode {

	/**
	 * The comparison operator in use.
	 */
	public Operator op;

	/**
	 * The target value.
	 */
	public FloatExpression target; // I'm gonna be honest, floats here seem like a terrible idea.

	/**
	 * Constructs a new {@code ComparisonExpressionNode} with default values.
	 */
	protected ComparisonExpressionNode() {}

	/**
	 * Decodes a {@code ComparisonExpressionNode} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			target.visit(visitor);
			visitor.visitEnd();
		}
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

	/**
	 * Represents the operators that can be used in a {@linkplain ComparisonExpressionNode comparison expression}.
	 */
	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Operator implements INamedEnum {

		/**
		 * Equals, or its Java operator equivalent: {@code ==}.
		 */
		EQUALS("Equal"),

		/**
		 * Not equals, or its Java operator equivalent: {@code !=}.
		 */
		NOT_EQUALS("NotEqual"),

		/**
		 * Less than, or its Java operator equivalent: {@code <}.
		 */
		LESS_THAN("Lesser"),

		/**
		 * Less than or equal to, or its Java operator equivalent: {@code <=}.
		 */
		LESS_THAN_OR_EQUAL_TO("LesserOrEqual"),

		/**
		 * Greater than, or its Java operator equivalent: {@code >}.
		 */
		GREATER_THAN("Greater"),

		/**
		 * Greater than or equal to, or its Java operator equivalent: {@code >=}.
		 */
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