package net.syntactickitsune.furblorb.api.script.visual.impl.block;

import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.api.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

// Apparently this only represents the following loops:
//   while true do
//   for (;;) {
//   while eggs is eggs:
// (I suppose you just slap a break statement when you're done?)
//
// I'm going to be honest though, my favorite kind of loop probably has to be:
//   repeat with C running through ridiculous line comments:
@RegisterSerializable("CommandLoop")
public final class LoopStatement extends StatementBlockNode {

	public List<ScriptNode> body;

	public LoopStatement() {}

	public LoopStatement(Decoder in) {
		body = read("LoopBody", in);
	}

	@Override
	public void write(Encoder to) {
		write("LoopBody", body, to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LoopStatement a)) return false;
		return Objects.equals(body, a.body);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(body);
	}
}