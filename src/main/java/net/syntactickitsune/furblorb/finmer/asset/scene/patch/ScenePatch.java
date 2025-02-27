package net.syntactickitsune.furblorb.finmer.asset.scene.patch;

import java.util.Set;

import net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode;
import net.syntactickitsune.furblorb.finmer.io.IFurballSerializable;

public abstract class ScenePatch implements IFurballSerializable {

	public abstract Set<SceneNode.Properties> getAdditionalProperties();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}