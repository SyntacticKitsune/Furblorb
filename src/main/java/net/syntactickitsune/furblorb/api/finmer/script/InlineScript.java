package net.syntactickitsune.furblorb.api.finmer.script;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents an "inline" script, that is, a script which has been written in the same file.
 * Occasionally may also be known as a "snippet."
 * @see ExternalScript
 */
@RegisterSerializable("ScriptDataInline")
public final class InlineScript extends Script {

	/**
	 * The contents of the script.
	 */
	public String contents;

	/**
	 * Constructs a new {@code InlineScript} with no contents.
	 */
	public InlineScript() {}

	/**
	 * Decodes an {@code InlineScript} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 */
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