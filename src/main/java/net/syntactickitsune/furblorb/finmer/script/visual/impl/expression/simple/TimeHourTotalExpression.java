package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression.simple;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;

/**
 * <p>
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Returns the total number of hours passed on the clock."
 * </p>
 * <p>
 * This expression is only available from format version 20 (Finmer v1.0.1) onwards.
 * </p>
 */
@RegisterSerializable(value = "ConditionTimeHourTotal", since = 20)
public final class TimeHourTotalExpression extends ComparisonExpressionNode {

	/**
	 * Constructs a new {@code TimeHourTotalExpression} with default values.
	 */
	public TimeHourTotalExpression() {}

	/**
	 * Decodes a {@code TimeHourTotalExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public TimeHourTotalExpression(Decoder in) {
		super(in);
	}
}