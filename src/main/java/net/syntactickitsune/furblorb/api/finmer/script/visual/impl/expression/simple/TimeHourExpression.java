package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;

/**
 * <p>
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Returns only the hour portion of the current world clock time."
 * </p>
 * <p>
 * This expression is only available from format version 20 (Finmer v1.0.1) onwards.
 * </p>
 */
@RegisterSerializable(value = "ConditionTimeHour", since = 20)
public final class TimeHourExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code TimeHourExpression} with default values.
	 */
	public TimeHourExpression() {}

	/**
	 * Decodes a {@code TimeHourExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public TimeHourExpression(Decoder in) {
		super(in);
	}
}