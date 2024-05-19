package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Compares the player's species to some other value.
 */
@RegisterSerializable("ConditionPlayerSpecies")
public final class PlayerSpeciesExpression extends ExpressionNode {

	/**
	 * The value being compared against.
	 */
	public StringExpression target;

	/**
	 * Whether or not the comparison is case sensitive.
	 */
	public boolean caseSensitive;

	/**
	 * Constructs a new {@code PlayerSpeciesExpression} with default values.
	 */
	public PlayerSpeciesExpression() {}

	/**
	 * Decodes a {@code PlayerSpeciesExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerSpeciesExpression(Decoder in) {
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
		if (!(obj instanceof PlayerSpeciesExpression a)) return false;
		return caseSensitive == a.caseSensitive && Objects.equals(target, a.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(target, caseSensitive);
	}
}