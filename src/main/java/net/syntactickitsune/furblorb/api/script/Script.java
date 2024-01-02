package net.syntactickitsune.furblorb.api.script;

import net.syntactickitsune.furblorb.io.IFurballSerializable;

public sealed abstract class Script implements IFurballSerializable permits ExternalScript, InlineScript, VisualActionScript, VisualConditionScript {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}