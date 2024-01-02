package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandSetScene")
public final class SetSceneStatement extends StatementNode {

	public UUID sceneId;

	public SetSceneStatement() {}

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