package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * From <a href="https://docs.finmer.dev/script-reference/flow">the documentation</a>:
 * "Switches to a different scene."
 */
@RegisterSerializable("CommandSetScene")
public final class SetSceneStatement extends StatementNode {

	/**
	 * The ID of the scene to switch to.
	 */
	public UUID sceneId;

	/**
	 * Constructs a new {@code SetSceneStatement} with default values.
	 */
	public SetSceneStatement() {}

	/**
	 * Decodes a {@code SetSceneStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SetSceneStatement(Decoder in) {
		sceneId = in.readUUID("SceneGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("SceneGuid", sceneId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SetSceneStatement a)) return false;
		return Objects.equals(sceneId, a.sceneId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(sceneId);
	}
}