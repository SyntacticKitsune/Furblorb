package net.syntactickitsune.furblorb.api.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

/**
 * Represents a {@code String} expression which may be a literal {@code String}, a variable reference, or a script.
 */
public final class StringExpression implements IFurballSerializable {

	/**
	 * The mode, determining whether the expression is a literal, a variable reference, or a script.
	 */
	public ExpressionMode mode = ExpressionMode.LITERAL;

	/**
	 * The expression value, reference, or script.
	 */
	public String value;

	public StringExpression() {}

	public StringExpression(Decoder in) {
		mode = in.readEnum("OperandMode", ExpressionMode.class);
		value = in.readString("OperandText");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("OperandMode", mode);
		to.writeString("OperandText", value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof StringExpression a)) return false;
		return mode == a.mode && Objects.equals(value, a.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, value);
	}
}