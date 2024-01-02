package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandLog")
public final class LogStatement extends StatementNode {

	public String text = "";
	public boolean raw;

	public LogStatement() {}

	public LogStatement(Decoder in) {
		text = in.readString("Text");
		raw = in.readBoolean("IsRaw");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Text", text);
		to.writeBoolean("IsRaw", raw);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LogStatement a)) return false;
		return raw == a.raw && Objects.equals(text, a.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(raw, text);
	}
}