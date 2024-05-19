package net.syntactickitsune.furblorb.api.finmer.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents a {@code float} expression which may be a literal {@code float}, a variable reference, or a script.
 */
public final class FloatExpression implements IFurballSerializable {

	/**
	 * The mode, determining whether the expression is a literal, a variable reference, or a script.
	 */
	public ExpressionMode mode = ExpressionMode.LITERAL;

	/**
	 * The literal {@code float} value, for {@link ExpressionMode#LITERAL}.
	 */
	public float literal;

	/**
	 * The variable or script reference, for {@link ExpressionMode#VARIABLE} and {@link ExpressionMode#SCRIPT}, respectively.
	 */
	public String ref;

	/**
	 * Constructs a new {@code FloatExpression} with default values.
	 */
	public FloatExpression() {}

	/**
	 * Decodes a {@code FloatExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public FloatExpression(Decoder in) {
		mode = in.readEnum("OperandMode", ExpressionMode.class);
		if (mode == ExpressionMode.LITERAL)
			literal = in.readFloat("OperandLiteral");
		else
			ref = in.readString("OperandText");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("OperandMode", mode);
		if (mode == ExpressionMode.LITERAL)
			to.writeFloat("OperandLiteral", literal);
		else
			to.writeString("OperandText", ref);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof FloatExpression a)) return false;
		return mode == a.mode && literal == a.literal && Objects.equals(ref, a.ref);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, literal, ref);
	}
}