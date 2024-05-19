package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * <p>
 * From <a href="https://docs.finmer.dev/script-reference/world">the documentation</a>:
 * "Advances the time of the world clock, as returned by the {@code GetTime()} function."
 * </p>
 * <p>
 * This expression is only available from format version 20 (Finmer v1.0.1) onwards.
 * </p>
 */
@RegisterSerializable(value = "CommandTimeAdvance", since = 20)
public final class TimeAdvanceStatement extends StatementNode {

	/**
	 * The number of hours to advance by.
	 */
	public IntExpression hours = new IntExpression();

	/**
	 * Constructs a new {@code TimeAdvanceStatement} with default values.
	 */
	public TimeAdvanceStatement() {}

	/**
	 * Decodes a {@code TimeAdvanceStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public TimeAdvanceStatement(Decoder in) {
		hours = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		hours.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TimeAdvanceStatement a)) return false;
		return Objects.equals(hours, a.hours);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hours);
	}
}