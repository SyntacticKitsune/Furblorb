package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * <p>
 * The {@code InlineSnippetStatement} acts as a sort of bridge between the visual scripting framework and handwritten scripts.
 * It allows one to embed a handwritten script (called a "snippet") as a single statement.
 * </p>
 * <p>
 * The primary reason this exists is for inevitable situations when some Finmer (or module) API is not available through visual scripting,
 * as a way to still use it without needing to write a full script by hand.
 * </p>
 */
@RegisterSerializable("CommandInlineSnippet")
public final class InlineSnippetStatement extends StatementNode {

	/**
	 * The contents of the snippet.
	 */
	public String contents;

	/**
	 * Constructs a new {@code InlineSnippetStatement} with default values.
	 */
	public InlineSnippetStatement() {}

	/**
	 * Decodes a {@code InlineSnippetStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public InlineSnippetStatement(Decoder in) {
		contents = in.readString("Snippet");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Snippet", contents);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			visitor.visitCode(contents);
			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof InlineSnippetStatement a)) return false;
		return Objects.equals(contents, a.contents);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(contents);
	}
}