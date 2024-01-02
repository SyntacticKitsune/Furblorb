package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandSleep")
public final class SleepStatement extends StatementNode {

	public FloatExpression seconds = new FloatExpression();

	public SleepStatement() {}

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