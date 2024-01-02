package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPlayerSetHealth")
public final class PlayerSetHealthStatement extends StatementNode {

	public Strategy strategy;
	public IntExpression expression;

	public PlayerSetHealthStatement() {}

	public PlayerSetHealthStatement(Decoder in) {
		strategy = in.readEnum("ValueOperation", Strategy.class);
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("ValueOperation", strategy);
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetHealthStatement a)) return false;
		return strategy == a.strategy && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(strategy, expression);
	}

	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Strategy implements INamedEnum {

		ADD("Add"),
		SET("Set");

		private final String id;

		private Strategy(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}