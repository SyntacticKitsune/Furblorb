package net.syntactickitsune.furblorb.api.component;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.asset.CreatureAsset;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;

/**
 * Represents a single "remapping" rule for strings logged during combat.
 * @see CreatureAsset#strings
 */
public final class StringMapping {

	public String key;
	public Rule rule;
	public String newKey;

	/**
	 * Constructs a new {@code StringMapping} with default values.
	 */
	public StringMapping() {}

	/**
	 * Decodes a {@code StringMapping} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public StringMapping(Decoder in) {
		key = in.readString("Key");
		rule = in.readEnum("Rule", StringMapping.Rule.class);
		newKey = in.readString("NewKey");
	}

	/**
	 * Writes this {@code StringMapping} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeString("Key", key);
		to.writeEnum("Rule", rule);
		to.writeString("NewKey", newKey);
	}

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