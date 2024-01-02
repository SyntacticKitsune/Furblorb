package net.syntactickitsune.furblorb.api.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

public final class IntExpression implements IFurballSerializable {

	public ExpressionMode mode = ExpressionMode.LITERAL;
	public int literal;
	public String ref;

	public IntExpression() {}

	public IntExpression(Decoder in) {
		mode = in.readEnum("OperandMode", ExpressionMode.class);
		if (mode == ExpressionMode.LITERAL)
			literal = in.readInt("OperandLiteral");
		else
			ref = in.readString("OperandText");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("OperandMode", mode);
		if (mode == ExpressionMode.LITERAL)
			to.writeInt("OperandLiteral", literal);
		else
			to.writeString("OperandText", ref);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof IntExpression a)) return false;
		return mode == a.mode && literal == a.literal && Objects.equals(ref, a.ref);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, literal, ref);
	}
}