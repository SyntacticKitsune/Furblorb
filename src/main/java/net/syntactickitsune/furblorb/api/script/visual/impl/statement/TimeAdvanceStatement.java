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

	public TimeAdvanceStatement() {}

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