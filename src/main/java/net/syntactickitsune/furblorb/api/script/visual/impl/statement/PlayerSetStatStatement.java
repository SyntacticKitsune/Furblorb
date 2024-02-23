package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.api.script.visual.impl.statement.PlayerSetHealthStatement.Strategy;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Modifies one of the player's stats.
 */
@RegisterSerializable("CommandPlayerSetStat")
public final class PlayerSetStatStatement extends StatementNode {

	/**
	 * The stat to modify.
	 */
	public Stat stat;

	/**
	 * Determines whether to add or set the value.
	 */
	public Strategy strategy;

	/**
	 * The value to add or set.
	 */
	public IntExpression expression;

	public PlayerSetStatStatement() {}

	public PlayerSetStatStatement(Decoder in) {
		stat = in.readEnum("Stat", Stat.class);
		strategy = in.readEnum("StatOperation", Strategy.class);
		expression = new IntExpression(in);
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("Stat", stat);
		to.writeEnum("StatOperation", strategy);
		expression.write(to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetStatStatement a)) return false;
		return stat == a.stat && strategy == a.strategy && Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stat, strategy, expression);
	}

	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Stat implements INamedEnum {

		STRENGTH("Strength"),
		AGILITY("Agility"),
		BODY("Body"),
		WITS("Wits");

		private final String id;

		private Stat(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}