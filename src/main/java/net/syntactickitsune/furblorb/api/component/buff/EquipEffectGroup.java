package net.syntactickitsune.furblorb.api.component.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.asset.ItemAsset;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.IFurballSerializable;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * For equippable {@linkplain ItemAsset items}, this represents a single series of effects they might apply.
 */
@RegisterSerializable("EquipEffectGroup")
public final class EquipEffectGroup implements IFurballSerializable {

	/**
	 * Determines when the effect is applied.
	 */
	public Trigger trigger = Trigger.ALWAYS;

	/**
	 * Determines what the effect is applied to.
	 */
	public Target target = Target.SELF;

	/**
	 * Determines the chance of the effect being applied.
	 */
	public float chance = 1F;

	/**
	 * The key of a message to display when the effect is applied.
	 */
	public String stringKey;

	/**
	 * How many turns to apply the effect for.
	 */
	public int duration = 1;

	/**
	 * The list of buffs to apply.
	 */
	public final List<Buff> buffs = new ArrayList<>();

	/**
	 * Constructs a new {@code EquipEffectGroup} with default values.
	 */
	public EquipEffectGroup() {}

	/**
	 * Decodes an {@code EquipEffectGroup} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public EquipEffectGroup(Decoder in) {
		trigger = in.readEnum("ProcStyle", Trigger.class);
		if (trigger != Trigger.ALWAYS) {
			target = in.readEnum("ProcTarget", Target.class);
			chance = in.readFloat("ProcChance");
			stringKey = in.readString("ProcStringTableKey");
			duration = in.readInt("Duration");
		}

		buffs.addAll(in.readOptionalList("Buffs", FurballSerializables::read));
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("ProcStyle", trigger);
		if (trigger != Trigger.ALWAYS) {
			to.writeEnum("ProcTarget", target);
			to.writeFloat("ProcChance", chance);
			to.writeString("ProcStringTableKey", stringKey);
			to.writeInt("Duration", duration);
		}

		to.writeOptionalList("Buffs", buffs, Buff::writeWithId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof EquipEffectGroup a)) return false;
		return trigger == a.trigger && target == a.target && chance == a.chance
				&& duration == a.duration && Objects.equals(stringKey, a.stringKey)
				&& Objects.equals(buffs, a.buffs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(trigger, target, chance, stringKey, duration, buffs);
	}

	/**
	 * Represents different "triggers" for {@link EquipEffectGroup} application.
	 * These are various events in which an {@code EquipEffectGroup} may be applied.
	 */
	public static enum Trigger implements INamedEnum {

		/**
		 * The effect is always active.
		 */
		ALWAYS("Always"),

		/**
		 * The effect is applied when a round of combat starts.
		 */
		ROUND_START("RoundStart"),

		/**
		 * The effect is applied when a turn of combat starts.
		 */
		TURN_START("TurnStart"),

		/**
		 * The effect is applied when the wielder's attack hits.
		 */
		WIELDER_ATTACK_HIT("WielderAttackHit"),

		/**
		 * The effect is applied when the wielder's attack misses.
		 */
		WIELDER_ATTACK_MISS("WielderAttackMiss"),

		/**
		 * The effect is applied when the wielder is grappled.
		 */
		WIELDER_GRAPPLED("WielderGrappled"),

		/**
		 * The effect is applied when the wielder is swallowed.
		 */
		WIELDER_SWALLOWED("WielderSwallowed"),

		/**
		 * The effect is applied when the wielder swallows something.
		 */
		WIELDER_SWALLOWS_PREY("WielderSwallowsPrey"),

		/**
		 * The effect is applied when an enemy's attack hits the wielder.
		 */
		ENEMY_ATTACK_HIT("EnemyAttackHit"),

		/**
		 * The effect is applied when an enemy's attack misses the wielder.
		 */
		ENEMY_ATTACK_MISS("EnemyAttackMiss");

		private final String id;

		private Trigger(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}

	/**
	 * Represents the target of an {@link EquipEffectGroup}.
	 */
	public static enum Target implements INamedEnum {

		/**
		 * The effect is applied to the wielder.
		 */
		SELF("Self"),

		/**
		 * The effect is applied to the wielder's current opponent.
		 */
		OPPONENT("Opponent"),

		/**
		 * The effect is applied to all allies (including the wielder).
		 */
		ALL_ALLIES("AllAllies"),

		/**
		 * The effect is applied to all opponents.
		 */
		ALL_OPPONENTS("AllOpponents");

		private final String id;

		private Target(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}