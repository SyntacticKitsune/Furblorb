package net.syntactickitsune.furblorb.api.script.visual;

import net.syntactickitsune.furblorb.io.IFurballSerializable;

public abstract class ScriptNode implements IFurballSerializable {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}