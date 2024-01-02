package net.syntactickitsune.furblorb.api.component;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.INamedEnum;

public final class StringMapping {

	public String key;
	public Rule rule;
	public String newKey;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof StringMapping a)) return false;
		return rule == a.rule && Objects.equals(key, a.key) && Objects.equals(newKey, a.newKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, rule, newKey);
	}

	public static enum Rule implements INamedEnum {

		ALWAYS("Always"),
		NPC_TO_PLAYER("NpcToPlayer"),
		PLAYER_TO_NPC("PlayerToNpc"),
		NPC_TO_NPC_AS_INSTIGATOR("NpcToNpcAsInstigator"),
		NPC_TO_NPC_AS_TARGET("NpcToNpcAsTarget");

		private final String id;

		private Rule(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}