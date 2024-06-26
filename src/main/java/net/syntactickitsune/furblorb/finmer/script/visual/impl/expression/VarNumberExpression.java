package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/storage">the documentation</a>:
 * "Returns a number from storage."
 */
@RegisterSerializable("ConditionVarNumber")
public final class VarNumberExpression extends ExpressionNode {

	/**
	 * The name of the number to retrieve.
	 */
	public String variable;

	/**
	 * The comparison to perform on the stored number.
	 */
	// HACK: We use composition instead of inheritance because otherwise deserialization is impossible.
	public ComparisonExpressionNode comparison = new RawComparisonExpressionNode();

	/**
	 * Constructs a new {@code VarNumberExpression} with default values.
	 */
	public VarNumberExpression() {}

	/**
	 * Decodes a {@code VarNumberExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarNumberExpression(Decoder in) {
		variable = in.readString("VariableName");
		comparison = new RawComparisonExpressionNode(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		comparison.write(to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			comparison.visit(visitor);
			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarNumberExpression a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(comparison, a.comparison);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, comparison);
	}

	private static final class RawComparisonExpressionNode extends ComparisonExpressionNode {

		public RawComparisonExpressionNode() {}

		public RawComparisonExpressionNode(Decoder in) {
			super(in);
		}
	}
}