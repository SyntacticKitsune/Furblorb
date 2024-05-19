package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement.PlayerSetHealthStatement.Strategy;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;

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

	/**
	 * Constructs a new {@code PlayerSetStatStatement} with default values.
	 */
	public PlayerSetStatStatement() {}

	/**
	 * Decodes a {@code PlayerSetStatStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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

	/**
	 * Identifies the stat being modified in the {@link PlayerSetStatStatement}.
	 */
	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Stat implements INamedEnum {

		/**
		 * The strength stat is being modified.
		 */
		STRENGTH("Strength"),

		/**
		 * The agility stat is being modified.
		 */
		AGILITY("Agility"),

		/**
		 * The body stat is being modified.
		 */
		BODY("Body"),

		/**
		 * The wits stat is being modified.
		 */
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