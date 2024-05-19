package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;

/**
 * Modifies the player's current health.
 */
@RegisterSerializable("CommandPlayerSetHealth")
public final class PlayerSetHealthStatement extends StatementNode {

	/**
	 * Determines whether to add or set the value.
	 */
	public Strategy strategy;

	/**
	 * The value to add or set.
	 */
	public IntExpression expression;

	/**
	 * Constructs a new {@code PlayerSetHealthStatement} with default values.
	 */
	public PlayerSetHealthStatement() {}

	/**
	 * Decodes a {@code PlayerSetHealthStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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

	/**
	 * Determines whether to add or set a value.
	 */
	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum Strategy implements INamedEnum {

		/**
		 * The value should be added to the existing one.
		 */
		ADD("Add"),

		/**
		 * The value should overwrite the existing one.
		 */
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