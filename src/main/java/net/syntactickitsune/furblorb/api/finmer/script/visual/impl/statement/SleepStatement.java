package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Pauses execution of the script for the specified time."
 */
@RegisterSerializable("CommandSleep")
public final class SleepStatement extends StatementNode {

	/**
	 * The number of seconds to pause for.
	 */
	public FloatExpression seconds = new FloatExpression();

	/**
	 * Constructs a new {@code SleepStatement} with default values.
	 */
	public SleepStatement() {}

	/**
	 * Decodes a {@code SleepStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SleepStatement(Decoder in) {
		seconds = new FloatExpression(in);
	}

	@Override
	public void write(Encoder to) {
		seconds.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SleepStatement a)) return false;
		return Objects.equals(seconds, a.seconds);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(seconds);
	}
}