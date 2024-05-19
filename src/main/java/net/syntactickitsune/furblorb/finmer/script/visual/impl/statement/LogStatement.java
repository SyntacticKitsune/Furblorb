package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Logs some message to the log pane.
 * The {@code raw} parameter determines whether the message is logged verbatim, rather than interpreted as the name of a string.
 */
@RegisterSerializable("CommandLog")
public final class LogStatement extends StatementNode {

	/**
	 * The message to log.
	 */
	public String text = "";

	/**
	 * Whether to log the message verbatim instead of looking it up in a string table.
	 */
	public boolean raw;

	/**
	 * Constructs a new {@code LogStatement} with default values.
	 */
	public LogStatement() {}

	/**
	 * Decodes a {@code LogStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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