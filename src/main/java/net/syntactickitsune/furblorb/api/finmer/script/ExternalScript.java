package net.syntactickitsune.furblorb.api.finmer.script;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Represents an "external" script, which is a script whose contents are defined in some other file.
 * @see InlineScript
 */
@RegisterSerializable("ScriptDataExternal")
public final class ExternalScript extends Script {

	/**
	 * The name of the associated file.
	 */
	public String name;

	/**
	 * The contents of the file.
	 */
	public String contents;

	/**
	 * Constructs a new {@code ExternalScript} with no associated file.
	 */
	public ExternalScript() {}

	/**
	 * Decodes an {@code ExternalScript} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 */
	public ExternalScript(Decoder in) {
		name = in.readString("Name");
		final byte[] bytes = in.readExternal(name + ".lua", Decoder::readByteArray, Function.identity());
		contents = bytes != null ? new String(bytes, StandardCharsets.UTF_8) : "";
		contents = contents.replace("\r\n", "\n").replace("\n", "\r\n"); // Fix line endings.
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Name", name);
		to.writeExternal(name + ".lua", contents.isBlank() ? null : contents.getBytes(StandardCharsets.UTF_8), (key, v, enc) -> enc.writeByteArray(key, v), Function.identity());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ExternalScript a)) return false;
		return Objects.equals(name, a.name) && Objects.equals(contents, a.contents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, contents);
	}
}