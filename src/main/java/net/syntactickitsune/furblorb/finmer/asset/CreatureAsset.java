package net.syntactickitsune.furblorb.finmer.asset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.FurballUtil;
import net.syntactickitsune.furblorb.finmer.component.CreatureFlags;
import net.syntactickitsune.furblorb.finmer.component.StringMapping;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;

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
	 * <p>The creature's strength stat. This determines how many attack and grapple dice the creature has.</p>
	 * <p>The editor limits this value to between 1 and 100, so it is an error to exceed these limits.</p>
	 */
	// Power creep go brrr.
	public int strength = 1;

	/**
	 * <p>The creature's agility stat. This determines how many defense and struggle dice the creature has.</p>
	 * <p>The editor limits this value to between 1 and 100, so it is an error to exceed these limits.</p>
	 */
	public int agility = 1;

	/**
	 * <p>The creature's body stat. This determines both how much HP and how many swallow dice the creature has.</p>
	 * <p>The editor limits this value to between 1 and 100, so it is an error to exceed these limits.</p>
	 */
	public int body = 1;

	/**
	 * <p>The creature's wits stat. This determines the turn order, but is also occasionally used for checks.</p>
	 * <p>The editor limits this value to between 1 and 100, so it is an error to exceed these limits.</p>
	 */
	public int wits = 1;

	/**
	 * Various "flags" on the creature.
	 */
	public CreatureFlags flags = new CreatureFlags();

	/**
	 * <p>The creature's level.</p>
	 * <p>The editor limits this value to between 1 and 40, so it is an error to exceed these limits.</p>
	 */
	public int level = 1;

	/**
	 * The creature's size.
	 * A creature can only grapple creatures of equal or smaller size,
	 * and do not need as many vore check rounds against smaller creatures.
	 */
	public Size size = Size.MEDIUM;

	/**
	 * The creature's gender.
	 * This mostly determines pronouns.
	 */
	public Gender gender = Gender.MALE;

	/**
	 * A list of item asset {@code UUIDs} representing the creature's equipment.
	 * There may be one weapon, one armor piece, and two accessories.
	 * The weapon <i>must</i> be the first item in the list, however the order of the others doesn't matter.
	 * An {@linkplain FurballUtil#EMPTY_UUID empty ID} means there is no item in that slot.
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

	/**
	 * Constructs a new {@code CreatureAsset} with default values.
	 */
	public CreatureAsset() {}

	/**
	 * Decodes a {@code CreatureAsset} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CreatureAsset(Decoder in) {
		super(in);

		// I'm already getting flashbacks to Nomad's serialization code.
		objectName = in.readString("ObjectName");
		objectAlias = in.readString("ObjectAlias");
		strength = in.readInt("Strength");
		agility = in.readInt("Agility");
		body = in.readInt("Body");
		wits = in.readInt("Wits");
		flags = new CreatureFlags(in.readInt("Flags"));
		level = in.readInt("Level");
		size = in.readEnum("Size", Size.class);
		gender = in.readEnum("Gender", Gender.class);

		if (in.validate()) {
			FurballUtil.checkInRange("Strength", strength, 1, 100);
			FurballUtil.checkInRange("Agility", agility, 1, 100);
			FurballUtil.checkInRange("Body", body, 1, 100);
			FurballUtil.checkInRange("Wits", wits, 1, 100);
			FurballUtil.checkInRange("Level", level, 1, 40);
		}

		for (int i = 0; i < equipment.length; i++)
			equipment[i] = in.readUUID("Equipment" + (i + 1));

		predatorEnabled = in.readBoolean("IsPredator");
		autoSwallowedByPlayer = in.readBoolean("AutoVorePrey");
		if (predatorEnabled) {
			autoSwallowPlayer = in.readBoolean("AutoVorePredator");
			predatorDigests = in.readBoolean("PredatorDigests");
			predatorDisposal = in.readBoolean("PredatorDisposal");
		}

		strings.addAll(in.readObjectList("StringMappings", StringMapping::new));
	}

	// Cursed thought: what if this was a `com.mojang.serialization.Codec`?
	@Override
	protected void write0(Encoder to) {
		if (to.validate()) {
			FurballUtil.checkInRange("Strength", strength, 1, 100);
			FurballUtil.checkInRange("Agility", agility, 1, 100);
			FurballUtil.checkInRange("Body", body, 1, 100);
			FurballUtil.checkInRange("Wits", wits, 1, 100);
			FurballUtil.checkInRange("Level", level, 1, 40);
		}

		to.writeString("ObjectName", objectName);
		to.writeString("ObjectAlias", objectAlias);
		to.writeInt("Strength", strength);
		to.writeInt("Agility", agility);
		to.writeInt("Body", body);
		to.writeInt("Wits", wits);
		to.writeInt("Flags", flags.pack());
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

		to.writeObjectList("StringMappings", strings, StringMapping::write);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CreatureAsset a)) return false;
		return id.equals(a.id) && filename.equals(a.filename) && autoSwallowedByPlayer == a.autoSwallowedByPlayer
				&& autoSwallowPlayer == a.autoSwallowPlayer && predatorDisposal == a.predatorDisposal
				&& predatorDigests == a.predatorDigests && predatorEnabled == a.predatorEnabled && gender == a.gender
				&& size == a.size && level == a.level && wits == a.wits && body == a.body
				&& agility == a.agility && strength == a.strength && Objects.equals(flags, a.flags)
				&& Objects.equals(strings, a.strings) && Arrays.equals(equipment, a.equipment)
				&& Objects.equals(objectAlias, a.objectAlias) && Objects.equals(objectName, a.objectName);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(new Object[] {
				id, filename, objectName, objectAlias, strength, agility, body, wits, flags, level,
				size, gender, equipment, strings, predatorEnabled, predatorDigests, predatorDisposal,
				autoSwallowPlayer, autoSwallowedByPlayer
		});
	}

	/**
	 * Represents a vague "size" of a creature.
	 * The size determines how many successful rolls are needed to swallow a creature,
	 * and whether a creature can be swallowed.
	 */
	public static enum Size implements INamedEnum {

		/**
		 * The smallest size -- two sizes smaller than {@link #MEDIUM} (the default).
		 */
		TINY("Tiny"),

		/**
		 * In between {@link #TINY} and {@link #MEDIUM}.
		 */
		SMALL("Small"),

		/**
		 * The default size of all creatures.
		 */
		MEDIUM("Medium"),

		/**
		 * In between {@link #MEDIUM} and {@link #HUGE}.
		 */
		LARGE("Large"),

		/**
		 * The largest size -- two sizes larger than {@link #MEDIUM} (the default).
		 */
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

	/**
	 * Represents the genders of creatures.
	 */
	public static enum Gender implements INamedEnum {

		/**
		 * The creature is male, so he/him pronouns are in use.
		 */
		MALE("Male"),

		/**
		 * The creature is female, so she/her pronouns are in use.
		 */
		FEMALE("Female"),

		/**
		 * The creature is neutral, so they/them pronouns are in use.
		 */
		NEUTRAL("Neutral"),

		/**
		 * The creature is ungendered.
		 * In this case that means that it/its pronouns are used.
		 */
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