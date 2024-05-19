package net.syntactickitsune.furblorb.finmer.script.visual.impl.expression;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * <p>
 * The {@code InlineSnippetExpression} acts as a sort of bridge between the visual scripting framework and handwritten scripts.
 * It allows one to embed a handwritten script (called a "snippet") as the contents of an expression.
 * </p>
 * <p>
 * The primary reason this exists is for inevitable situations when some Finmer (or module) API is not available through visual scripting,
 * as a way to still use it without needing to write a full script by hand.
 * </p>
 */
@RegisterSerializable("ConditionInlineSnippet")
public final class InlineSnippetExpression extends ExpressionNode {

	/**
	 * The contents of the snippet.
	 */
	public String expression;

	/**
	 * Constructs a new {@code InlineSnippetExpression} with default values.
	 */
	public InlineSnippetExpression() {}

	/**
	 * Decodes a {@code InlineSnippetExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public InlineSnippetExpression(Decoder in) {
		expression = in.readString("Snippet");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Snippet", expression);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof InlineSnippetExpression a)) return false;
		return Objects.equals(expression, a.expression);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expression);
	}
}