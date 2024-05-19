package net.syntactickitsune.furblorb.api.finmer.component.buff;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.finmer.asset.CreatureAsset;
import net.syntactickitsune.furblorb.api.finmer.asset.ItemAsset;
import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * The class containing all the different {@link Buff} implementations.
 * @author SyntacticKitsune
 */
public final class Buffs {

	/**
	 * A {@link Buff} adding or removing some number of attack dice from a {@linkplain CreatureAsset creature}.
	 */
	@RegisterSerializable("BuffAttackDice")
	public static final class AttackDice extends Buff.Number {

		/**
		 * Constructs a new {@code AttackDice} buff with default values.
		 */
		public AttackDice() {}

		/**
		 * Decodes a {@code AttackDice} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public AttackDice(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} adding or removing some number of defense dice from a {@linkplain CreatureAsset creature}.
	 */
	@RegisterSerializable("BuffDefenseDice")
	public static final class DefenseDice extends Buff.Number {

		/**
		 * Constructs a new {@code DefenseDice} buff with default values.
		 */
		public DefenseDice() {}

		/**
		 * Decodes a {@code DefenseDice} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public DefenseDice(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} adding or removing some number of grapple dice from a {@linkplain CreatureAsset creature}.
	 */
	@RegisterSerializable("BuffGrappleDice")
	public static final class GrappleDice extends Buff.Number {

		/**
		 * Constructs a new {@code GrappleDice} buff with default values.
		 */
		public GrappleDice() {}

		/**
		 * Decodes a {@code GrappleDice} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public GrappleDice(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} adding or removing some number of swallow dice from a {@linkplain CreatureAsset creature}.
	 */
	@RegisterSerializable("BuffSwallowDice")
	public static final class SwallowDice extends Buff.Number {

		/**
		 * Constructs a new {@code SwallowDice} buff with default values.
		 */
		public SwallowDice() {}

		/**
		 * Decodes a {@code SwallowDice} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public SwallowDice(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} adding or removing some number of struggle dice from a {@linkplain CreatureAsset creature}.
	 */
	@RegisterSerializable("BuffStruggleDice")
	public static final class StruggleDice extends Buff.Number {

		/**
		 * Constructs a new {@code StruggleDice} buff with default values.
		 */
		public StruggleDice() {}

		/**
		 * Decodes a {@code StruggleDice} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public StruggleDice(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} modifying a {@linkplain CreatureAsset creature}'s maximum health.
	 */
	@RegisterSerializable("BuffHealth")
	public static final class Health extends Buff.Number {

		/**
		 * Constructs a new {@code Health} buff with default values.
		 */
		public Health() {}

		/**
		 * Decodes a {@code Health} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public Health(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} modifying a {@linkplain CreatureAsset creature}'s current health over every turn the buff is active.
	 */
	@RegisterSerializable("BuffHealthOverTime")
	public static final class Regeneration extends Buff.Number {

		/**
		 * Constructs a new {@code Regeneration} buff with default values.
		 */
		public Regeneration() {}

		/**
		 * Decodes a {@code Regeneration} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public Regeneration(Decoder in) { super(in); }
	}

	/**
	 * A {@link Buff} that prevents a {@linkplain CreatureAsset creature} from taking any turns in combat for every turn the buff is active.
	 */
	@RegisterSerializable("BuffStun")
	public static final class Stun extends Buff {

		/**
		 * Constructs a new {@code Stun} buff with default values.
		 */
		public Stun() {}

		/**
		 * Decodes a {@code Stun} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public Stun(Decoder in) {}
		@Override
		public void write(Encoder to) {}
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Stun;
		}
		@Override
		public int hashCode() {
			return 0;
		}
	}

	/**
	 * A {@link Buff} adding some text to an {@linkplain ItemAsset item}'s tooltip.
	 */
	@RegisterSerializable("BuffCustomTooltipText")
	public static final class Tooltip extends Buff {

		/**
		 * The text added to the tooltip.
		 */
		public String text;

		/**
		 * Constructs a new {@code Tooltip} buff with default values.
		 */
		public Tooltip() {}

		/**
		 * Decodes a {@code Tooltip} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public Tooltip(Decoder in) {
			text = in.readString("TooltipText");
		}

		@Override
		public void write(Encoder to) {
			to.writeString("TooltipText", text);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Tooltip t)) return false;
			return Objects.equals(text, t.text);
		}

		@Override
		public int hashCode() {
			return text.hashCode();
		}
	}
}