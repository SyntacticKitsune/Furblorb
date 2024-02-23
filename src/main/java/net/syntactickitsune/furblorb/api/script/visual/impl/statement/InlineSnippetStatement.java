package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	public InlineSnippetStatement() {}

	public InlineSnippetStatement(Decoder in) {
		contents = in.readString("Snippet");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Snippet", contents);
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