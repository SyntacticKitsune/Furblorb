package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	// HACK: We use composition instead of inheritance because otherwise deserialization is impossible.
	public ComparisonExpressionNode comparison = new RawComparisonExpressionNode();

	public VarNumberExpression() {}

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