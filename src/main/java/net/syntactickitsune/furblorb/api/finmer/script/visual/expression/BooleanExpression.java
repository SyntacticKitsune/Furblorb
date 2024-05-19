package net.syntactickitsune.furblorb.api.finmer.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents a {@code boolean} expression which may be a literal {@code boolean}, a variable reference, or a script.
 */
public final class BooleanExpression implements IFurballSerializable {

	/**
	 * The mode, determining whether the expression is a literal, a variable reference, or a script.
	 */
	public ExpressionMode mode = ExpressionMode.LITERAL;

	/**
	 * The literal {@code boolean} value, for {@link ExpressionMode#LITERAL}.
	 */
	public boolean literal;

	/**
	 * The variable or script reference, for {@link ExpressionMode#VARIABLE} and {@link ExpressionMode#SCRIPT}, respectively.
	 */
	public String ref;

	/**
	 * Constructs a new {@code BooleanExpression} with default values.
	 */
	public BooleanExpression() {}

	/**
	 * Decodes a {@code BooleanExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public BooleanExpression(Decoder in) {
		mode = in.readEnum("OperandMode", ExpressionMode.class);
		if (mode == ExpressionMode.LITERAL)
			literal = in.readBoolean("OperandLiteral");
		else
			ref = in.readString("OperandText");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("OperandMode", mode);
		if (mode == ExpressionMode.LITERAL)
			to.writeBoolean("OperandLiteral", literal);
		else
			to.writeString("OperandText", ref);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof BooleanExpression a)) return false;
		return mode == a.mode && literal == a.literal && Objects.equals(ref, a.ref);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, literal, ref);
	}
}