package net.syntactickitsune.furblorb.api.script;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Represents one of Finmer's "inline scripts."
 * Inline here is in the sense of what say, DFU considers inline -- something defined in the same file, rather than in another.
 */
@RegisterSerializable("ScriptDataInline")
public final class InlineScript extends Script {

	public String contents;

	public InlineScript() {}

	public InlineScript(Decoder in) {
		contents = in.readString("Script");
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Script", contents);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof InlineScript a)) return false;
		return Objects.equals(contents, a.contents);
	}

	@Override
	public int hashCode() {
		return contents.hashCode();
	}
}