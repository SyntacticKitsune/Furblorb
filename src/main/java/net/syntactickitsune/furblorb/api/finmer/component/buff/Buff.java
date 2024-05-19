package net.syntactickitsune.furblorb.api.finmer.component.buff;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.IFurballSerializable;

public sealed abstract class Buff implements IFurballSerializable permits Buff.Number, Buffs.Stun, Buffs.Tooltip {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	static sealed abstract class Number extends Buff permits Buffs.AttackDice, Buffs.DefenseDice, Buffs.GrappleDice, Buffs.SwallowDice, Buffs.StruggleDice, Buffs.Health, Buffs.Regeneration {

		/**
		 * The number associated with the buff.
		 */
		public int number;

		/**
		 * Constructs a new {@code Number} buff with default values.
		 */
		protected Number() {}

		/**
		 * Decodes a {@code Number} buff from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		protected Number(Decoder in) {
			number = in.readInt("Delta");
		}

		@Override
		public void write(Encoder to) {
			to.writeInt("Delta", number);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			final Number n = (Number) obj;
			return number == n.number;
		}

		@Override
		public int hashCode() {
			return Objects.hash(number);
		}
	}
}