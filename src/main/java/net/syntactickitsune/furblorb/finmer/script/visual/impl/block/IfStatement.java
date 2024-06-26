package net.syntactickitsune.furblorb.finmer.script.visual.impl.block;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.finmer.script.visual.expression.LogicalExpression;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Represents a Lua if-statement, optionally with an else branch.
 */
@RegisterSerializable("CommandIf")
public final class IfStatement extends StatementBlockNode {

	/**
	 * The if-statement's condition.
	 */
	public LogicalExpression expression;

	/**
	 * Whether or not the if-statement has an else branch.
	 */
	public boolean hasElseBranch; // You know, this could just be detected by whether the else body has anything in it.

	/**
	 * The contents of the if-statement.
	 */
	public List<ScriptNode> body;

	/**
	 * The contents of the if-statement's else branch.
	 */
	@Nullable
	public List<ScriptNode> elseBody;

	/**
	 * Constructs a new {@code IfStatement} with default values.
	 */
	public IfStatement() {}

	/**
	 * Decodes a {@code IfStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public IfStatement(Decoder in) {
		expression = new LogicalExpression(in);
		hasElseBranch = in.readBoolean("HasElseBranch");
		body = read("MainSubgroup", in);
		if (hasElseBranch)
			elseBody = read("ElseSubgroup", in);
	}

	@Override
	public void write(Encoder to) {
		expression.write(to);
		to.writeBoolean("HasElseBranch", hasElseBranch);
		write("MainSubgroup", body, to);
		if (hasElseBranch)
			write("ElseSubgroup", elseBody, to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			for (ScriptNode sn : body)
				sn.visit(visitor);

			if (elseBody != null)
				for (ScriptNode sn : elseBody)
					sn.visit(visitor);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof IfStatement a)) return false;
		return hasElseBranch == a.hasElseBranch && Objects.equals(expression, a.expression)
				&& Objects.equals(body, a.body) && Objects.equals(elseBody, a.elseBody);
	}

	@Override
	public int hashCode() {
		return Objects.hash(expression, hasElseBranch, body, elseBody);
	}
}