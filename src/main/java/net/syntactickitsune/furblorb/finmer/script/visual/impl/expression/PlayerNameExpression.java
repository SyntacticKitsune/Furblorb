package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Compares the player's name to some other value.
 */
@RegisterSerializable("ConditionPlayerName")
public final class PlayerNameExpression extends ExpressionNode {

	/**
	 * The value being compared against.
	 */
	public StringExpression target;

	/**
	 * Whether or not the comparison is case sensitive.
	 */
	public boolean caseSensitive;

	/**
	 * Constructs a new {@code PlayerNameExpression} with default values.
	 */
	public PlayerNameExpression() {}

	/**
	 * Decodes a {@code PlayerNameExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			target.visit(visitor);
			visitor.visitEnd();
		}
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