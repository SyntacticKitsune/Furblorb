package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * <p>
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Advances the time of the world clock so that the current hour matches the input hour number."
 * </p>
 * <p>
 * This expression is only available from format version 20 (Finmer v1.0.1) onwards.
 * </p>
 */
@RegisterSerializable(value = "CommandTimeSetHour", since = 20)
public final class TimeSetHourStatement extends StatementNode {

	public IntExpression hour = new IntExpression();

	/**
	 * Constructs a new {@code TimeSetHourStatement} with default values.
	 */
	public TimeSetHourStatement() {}

	/**
	 * Decodes a {@code TimeSetHourStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public TimeSetHourStatement(Decoder in) {
		hour = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		hour.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TimeSetHourStatement a)) return false;
		return Objects.equals(hour, a.hour);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hour);
	}
}