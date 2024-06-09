package net.syntactickitsune.furblorb.finmer.script.visual.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;
import net.syntactickitsune.furblorb.io.ParsingStrategy;
import net.syntactickitsune.furblorb.io.ParsingStrategy.NumberType;

/**
 * Represents a {@code boolean} expression like inside an if-statement.
 */
public final class LogicalExpression implements IFurballSerializable {

	/**
	 * The conditions being tested.
	 */
	public final List<ExpressionNode> conditions = new ArrayList<>();

	/**
	 * The mode: either "and" or "or".
	 */
	public Mode mode = Mode.AND;

	/**
	 * The target value, i.e. what the value of the expression should be for the check to succeed.
	 */
	public boolean target = true;

	/**
	 * Constructs a new {@code LogicalExpression} with default values.
	 */
	public LogicalExpression() {}

	/**
	 * Decodes a {@code LogicalExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LogicalExpression(Decoder in) {
		mode = in.readEnum("Mode", Mode.class);
		target = in.readBoolean("Operand");

		conditions.addAll(in.readOptionalObjectList("Tests", FurballSerializables::read));
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Mode", mode);
		to.writeBoolean("Operand", target);

		to.writeOptionalObjectList("Tests", conditions, ExpressionNode::writeWithId);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this)) {
			for (ExpressionNode en : conditions)
				en.visit(visitor);
			visitor.visitEnd();
		}
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

	/**
	 * Represents the modes of a {@link LogicalExpression}.
	 */
	@ParsingStrategy(NumberType.INT)
	public static enum Mode implements INamedEnum {

		/**
		 * Logical AND: all of the conditions must be {@code true} for the overall result to be {@code true}.
		 * In other words: <code>a&nbsp;&amp;&amp;&nbsp;b&nbsp;&amp;&amp;&nbsp;c</code>.
		 */
		AND("All"),

		/**
		 * Logical OR: if any of the conditions are {@code true}, the overall result is {@code true}.
		 * In other words: <code>a&nbsp;||&nbsp;b&nbsp;||&nbsp;c</code>.
		 */
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