package net.syntactickitsune.furblorb.api.component.buff;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

public final class Buffs {

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

	@RegisterSerializable("BuffCustomTooltipText")
	public static final class Tooltip extends Buff {

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