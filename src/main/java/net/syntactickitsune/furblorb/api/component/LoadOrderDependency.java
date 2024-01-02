package net.syntactickitsune.furblorb.api.component;

import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.INamedEnum;

public record LoadOrderDependency(UUID targetAsset, Relation relation) {

	public static enum Relation implements INamedEnum {

		BEFORE("Before"),
		AFTER("After");

		private final String id;

		private Relation(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}