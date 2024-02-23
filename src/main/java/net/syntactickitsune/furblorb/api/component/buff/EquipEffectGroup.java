package net.syntactickitsune.furblorb.api.component.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.IFurballSerializable;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("EquipEffectGroup")
public final class EquipEffectGroup implements IFurballSerializable {

	public Trigger trigger = Trigger.ALWAYS;
	public Target target = Target.SELF;
	public float chance = 1F;
	public String stringKey;
	public int duration = 1;
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

	public static enum Trigger implements INamedEnum {

		ALWAYS("Always"),
		ROUND_START("RoundStart"),
		TURN_START("TurnStart"),
		WIELDER_ATTACK_HIT("WielderAttackHit"),
		WIELDER_ATTACK_MISS("WielderAttackMiss"),
		WIELDER_GRAPPLED("WielderGrappled"),
		WIELDER_SWALLOWED("WielderSwallowed"),
		WIELDER_SWALLOWS_PREY("WielderSwallowsPrey"),
		ENEMY_ATTACK_HIT("EnemyAttackHit"),
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

	public static enum Target implements INamedEnum {

		SELF("Self"),
		OPPONENT("Opponent"),
		ALL_ALLIES("AllAllies"),
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