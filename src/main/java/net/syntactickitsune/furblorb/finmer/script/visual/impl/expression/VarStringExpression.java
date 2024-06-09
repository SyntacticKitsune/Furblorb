package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/storage">the documentation</a>:
 * "Returns a string from storage."
 */
@RegisterSerializable("ConditionVarString")
public final class VarStringExpression extends ExpressionNode {

	/**
	 * The name of the string to retrieve.
	 */
	public String variable;

	/**
	 * The expression to compare the string to.
	 */
	public StringExpression expression;

	/**
	 * Constructs a new {@code VarStringExpression} with default values.
	 */
	public VarStringExpression() {}

	/**
	 * Decodes a {@code VarStringExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public VarStringExpression(Decoder in) {
		variable = in.readString("VariableName");
		expression = new StringExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("VariableName", variable);
		expression.write(to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			expression.visit(visitor);
			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VarStringExpression a)) return false;
		return Objects.equals(variable, a.variable) && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variable, expression);
	}
}