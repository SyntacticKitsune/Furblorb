package net.syntactickitsune.furblorb.api.script.visual;

import net.syntactickitsune.furblorb.io.IFurballSerializable;

/**
 * The base class of all visual scripting nodes.
 * @author SyntacticKitsune
 */
public abstract class ScriptNode implements IFurballSerializable {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}