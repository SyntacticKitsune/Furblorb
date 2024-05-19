package net.syntactickitsune.furblorb.api.finmer.script.visual;

import net.syntactickitsune.furblorb.api.finmer.io.IFurballSerializable;

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