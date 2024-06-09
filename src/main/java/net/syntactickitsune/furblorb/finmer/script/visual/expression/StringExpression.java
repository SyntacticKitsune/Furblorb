package net.syntactickitsune.furblorb.finmer.script.visual.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

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

	/**
	 * Constructs a new {@code StringExpression} with default values.
	 */
	public StringExpression() {}

	/**
	 * Decodes a {@code StringExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this)) {
			switch (mode) {
				case LITERAL -> visitor.visitText(value);
				case SCRIPT -> visitor.visitCode(value);
				default -> {}
			}
			visitor.visitEnd();
		}
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