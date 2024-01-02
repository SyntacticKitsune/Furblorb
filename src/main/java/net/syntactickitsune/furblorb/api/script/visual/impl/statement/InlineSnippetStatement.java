package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandInlineSnippet")
public final class InlineSnippetStatement extends StatementNode {

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