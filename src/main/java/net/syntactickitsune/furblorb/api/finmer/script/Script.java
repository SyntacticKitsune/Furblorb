package net.syntactickitsune.furblorb.api.finmer.script;

import net.syntactickitsune.furblorb.io.IFurballSerializable;

/**
 * The base class of all kinds of scripts.
 * @author SyntacticKitsune
 */
public sealed abstract class Script implements IFurballSerializable permits ExternalScript, InlineScript, VisualActionScript, VisualConditionScript {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}