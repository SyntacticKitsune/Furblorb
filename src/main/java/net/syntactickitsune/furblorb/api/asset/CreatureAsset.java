package net.syntactickitsune.furblorb.api.asset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.component.CreatureFlags;
import net.syntactickitsune.furblorb.api.component.StringMapping;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.util.FurballUtil;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * A {@code CreatureAsset} represents something that may take part in a battle.
 * See also <a href="https://docs.finmer.dev/asset-types/creatures">the documentation</a>.
 */
@RegisterSerializable("AssetCreature")
public final class CreatureAsset extends FurballAsset {

	/**
	 * The name of the creature.
	 * This field name mirrors that of {@link ItemAsset}.
	 */
	public String objectName = "";

	/**
	 * An alternative name for the creature -- most frequently the definite article ("the") followed by {@link #objectName}.
	 * This field name mirrors that of {@link ItemAsset}.
	 */
	public String objectAlias = "";

	/**
	 * The creature's strength stat.
	 */
	// Power creep go brrr.
	public int strength = 0;

	/**
	 * The creature's agility stat.
	 */
	public int agility = 0;

	/**
	 * The creature's body stat.
	 */
	public int body = 0;

	/**
	 * The creature's wits stat.
	 */
	public int wits = 0;

	/**
	 * Various "flags" on the creature.
	 * Each flag is OR-ed with each other to compose this value.
	 * See {@link CreatureFlags} for a list.
	 */
	public int flags = 0;

	/**
	 * The creature's level.
	 */
	public int level = 1;

	/**
	 * The creature's size.
	 */
	public Size size = Size.MEDIUM;

	/**
	 * The creature's gender.
	 * This mostly determines pronouns.
	 */
	public Gender gender = Gender.MALE;

	/**
	 * A list of {@code UUIDs} representing the creature's equipment.
	 */
	public final UUID[] equipment = { FurballUtil.EMPTY_UUID, FurballUtil.EMPTY_UUID, FurballUtil.EMPTY_UUID, FurballUtil.EMPTY_UUID };

	/**
	 * A list of {@link StringMapping StringMappings}, which describe how to map various combat events to displayable text.
	 */
	public final List<StringMapping> strings = new ArrayList<>();

	/**
	 * Whether or not the creature can perform vore actions.
	 */
	public boolean predatorEnabled = true;

	/**
	 * Whether or not the creature deals digestion damage.
	 */
	public boolean predatorDigests = true;

	/**
	 * Whether or not the creature has a follow-up disposal scene.
	 */
	public boolean predatorDisposal = true;

	/**
	 * Whether the creature should swallow the player if they kill them first.
	 * This is basically a fail-safe for vore-only outcomes.
	 */
	public boolean autoSwallowPlayer = false;

	/**
	 * Whether the creature should be swallowed by the player if they kill it first.
	 * This is basically a fail-safe for vore-only outcomes.
	 */
	public boolean autoSwallowedByPlayer = false;

	public CreatureAsset() {}

	public CreatureAsset(Decoder in) {
		super(in);

		// I'm already getting flashbacks to Nomad's serialization code.
		objectName = in.readString("ObjectName");
		objectAlias = in.readString("ObjectAlias");
		strength = in.readInt("Strength");
		agility = in.readInt("Agility");
		body = in.readInt("Body");
		wits = in.readInt("Wits");
		flags = in.readInt("Flags");
		level = in.readInt("Level");
		size = in.readEnum("Size", Size.class);
		gender = in.readEnum("Gender", Gender.class);

		for (int i = 0; i < equipment.length; i++)
			equipment[i] = in.readUUID("Equipment" + (i + 1));

		predatorEnabled = in.readBoolean("IsPredator");
		autoSwallowedByPlayer = in.readBoolean("AutoVorePrey");
		if (predatorEnabled) {
			autoSwallowPlayer = in.readBoolean("AutoVorePredator");
			predatorDigests = in.readBoolean("PredatorDigests");
			predatorDisposal = in.readBoolean("PredatorDisposal");
		}

		strings.addAll(in.readList("StringMappings", StringMapping::new));
	}

	// Cursed thought: what if this was a `com.mojang.serialization.Codec`?
	@Override
	protected void write0(Encoder to) {
		to.writeString("ObjectName", objectName);
		to.writeString("ObjectAlias", objectAlias);
		to.writeInt("Strength", strength);
		to.writeInt("Agility", agility);
		to.writeInt("Body", body);
		to.writeInt("Wits", wits);
		to.writeInt("Flags", flags);
		to.writeInt("Level", level);
		to.writeEnum("Size", size);
		to.writeEnum("Gender", gender);

		for (int i = 0; i < equipment.length; i++)
			to.writeUUID("Equipment" + (i + 1), equipment[i]);

		to.writeBoolean("IsPredator", predatorEnabled);
		to.writeBoolean("AutoVorePrey", autoSwallowedByPlayer);
		if (predatorEnabled) { // Gotta shave off those three bytes! (Although technically this could all be packed into one byte...)
			to.writeBoolean("AutoVorePredator", autoSwallowPlayer);
			to.writeBoolean("PredatorDigests", predatorDigests);
			to.writeBoolean("PredatorDisposal", predatorDisposal);
		}

		to.writeList("StringMappings", strings, StringMapping::write);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CreatureAsset a)) return false;
		return id.equals(a.id) && filename.equals(a.filename) && autoSwallowedByPlayer == a.autoSwallowedByPlayer
				&& autoSwallowPlayer == a.autoSwallowPlayer && predatorDisposal == a.predatorDisposal
				&& predatorDigests == a.predatorDigests && predatorEnabled == a.predatorEnabled && gender == a.gender
				&& size == a.size && level == a.level && flags == a.flags && wits == a.wits && body == a.body
				&& agility == a.agility && strength == a.strength && Objects.equals(strings, a.strings)
				&& Arrays.equals(equipment, a.equipment) && Objects.equals(objectAlias, a.objectAlias)
				&& Objects.equals(objectName, a.objectName);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(new Object[] {
				id, filename, objectName, objectAlias, strength, agility, body, wits, flags, level,
				size, gender, equipment, strings, predatorEnabled, predatorDigests, predatorDisposal,
				autoSwallowPlayer, autoSwallowedByPlayer
		});
	}

	public static enum Size implements INamedEnum {

		TINY("Tiny"),
		SMALL("Small"),
		MEDIUM("Medium"),
		LARGE("Large"),
		HUGE("Huge");

		private final String id;

		private Size(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}

	public static enum Gender implements INamedEnum {

		MALE("Male"),
		FEMALE("Female"),
		NEUTRAL("Neutral"),
		UNGENDERED("Ungendered");

		private final String id;

		private Gender(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}