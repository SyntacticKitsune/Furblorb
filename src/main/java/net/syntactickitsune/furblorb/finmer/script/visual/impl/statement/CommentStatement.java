package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Not a statement in and of itself; it doesn't execute anything.
 * A {@code CommentStatement} is just that: a comment.
 */
@RegisterSerializable("CommandComment")
public final class CommentStatement extends StatementNode {

	/**
	 * The comment itself.
	 */
	public String comment;

	/**
	 * Constructs a new {@code CommentStatement} with default values.
	 */
	public CommentStatement() {}

	/**
	 * Decodes a {@code CommentStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CommentStatement(Decoder in) {
		comment = in.readString("Comment");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Comment", comment);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			visitor.visitText(comment);
			visitor.visitEnd();
		}
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