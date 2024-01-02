package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerName")
public final class PlayerNameExpression extends ExpressionNode {

	public StringExpression target;
	public boolean caseSensitive;

	public PlayerNameExpression() {}

	public PlayerNameExpression(Decoder in) {
		target = new StringExpression(in);
		caseSensitive = in.readBoolean("IsCaseSensitive");
	}

	@Override
	public void write(Encoder to) {
		target.write(to);
		to.writeBoolean("IsCaseSensitive", caseSensitive);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerNameExpression a)) return false;
		return caseSensitive == a.caseSensitive && Objects.equals(target, a.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(target, caseSensitive);
	}
}