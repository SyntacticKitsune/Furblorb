package net.syntactickitsune.furblorb.api.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

public final class FloatExpression implements IFurballSerializable {

	public ExpressionMode mode = ExpressionMode.LITERAL;
	public float literal;
	public String ref;

	public FloatExpression() {}

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