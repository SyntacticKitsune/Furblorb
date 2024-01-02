package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable(value = "CommandTimeSetHour", since = 20)
public final class TimeSetHourStatement extends StatementNode {

	public IntExpression hour = new IntExpression();

	public TimeSetHourStatement() {}

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