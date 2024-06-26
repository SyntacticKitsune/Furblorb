package net.syntactickitsune.furblorb.finmer.script;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.LogicalExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Represents a {@code boolean} expression built using Finmer's visual scripting framework.
 * Kinda like a {@link BooleanSupplier} -- yields a {@code boolean} given zero parameters.
 */
@RegisterSerializable("ScriptDataVisualCondition")
public final class VisualConditionScript extends Script {

	/**
	 * The {@code LogicalExpression} representing the overall condition.
	 */
	public LogicalExpression expression;

	/**
	 * Constructs a new {@code VisualConditionScript} with no condition.
	 */
	public VisualConditionScript() {}

	/**
	 * Decodes a {@code VisualConditionScript} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 */
	public VisualConditionScript(Decoder in) {
		expression = new LogicalExpression(in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this)) {
			expression.visit(visitor);
			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VisualConditionScript a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
}