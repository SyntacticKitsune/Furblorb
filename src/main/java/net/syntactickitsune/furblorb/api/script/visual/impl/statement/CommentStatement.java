package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandComment")
public final class CommentStatement extends StatementNode {

	public String comment;

	public CommentStatement() {}

	public CommentStatement(Decoder in) {
		comment = in.readString("Comment");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Comment", comment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CommentStatement a)) return false;
		return Objects.equals(comment, a.comment);
	}

	@Override
	public int hashCode() {
		return comment.hashCode();
	}
}