package net.syntactickitsune.furblorb.api.script.visual.impl.block;

import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.api.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.LogicalExpression;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandIf")
public final class IfStatement extends StatementBlockNode {

	public LogicalExpression expression;
	public boolean hasElseBranch; // You know, this could just be detected by whether the else body has anything in it.
	public List<ScriptNode> body;
	public List<ScriptNode> elseBody;

	public IfStatement() {}

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