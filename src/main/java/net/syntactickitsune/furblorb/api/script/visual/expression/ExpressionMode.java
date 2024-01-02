package net.syntactickitsune.furblorb.api.script.visual.expression;

import net.syntactickitsune.furblorb.api.io.INamedEnum;

public enum ExpressionMode implements INamedEnum {

	LITERAL("Literal"),
	VARIABLE("Variable"),
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