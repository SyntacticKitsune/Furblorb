package net.syntactickitsune.furblorb.finmer.script.visual.expression;

import net.syntactickitsune.furblorb.io.INamedEnum;

/**
 * Represents different expression "modes," which determine what the expression is.
 */
public enum ExpressionMode implements INamedEnum {

	/**
	 * The expression is a literal value.
	 */
	LITERAL("Literal"),

	/**
	 * The expression is a variable reference.
	 */
	VARIABLE("Variable"),

	/**
	 * The expression is a script.
	 */
	SCRIPT("Script");

	private final String id;

	private ExpressionMode(String id) {
		this.id = id;
	}

	@Override
	public String id() {
		return id;
	}
}