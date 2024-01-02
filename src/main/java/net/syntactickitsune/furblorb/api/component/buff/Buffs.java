package net.syntactickitsune.furblorb.api.component.buff;

import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

public final class Buffs {

	@RegisterSerializable("BuffAttackDice")
	public static final class AttackDice extends Buff.Number {
		public AttackDice() {}
		public AttackDice(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffDefenseDice")
	public static final class DefenseDice extends Buff.Number {
		public DefenseDice() {}
		public DefenseDice(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffGrappleDice")
	public static final class GrappleDice extends Buff.Number {
		public GrappleDice() {}
		public GrappleDice(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffSwallowDice")
	public static final class SwallowDice extends Buff.Number {
		public SwallowDice() {}
		public SwallowDice(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffStruggleDice")
	public static final class StruggleDice extends Buff.Number {
		public StruggleDice() {}
		public StruggleDice(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffHealth")
	public static final class Health extends Buff.Number {
		public Health() {}
		public Health(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffHealthOverTime")
	public static final class Regeneration extends Buff.Number {
		public Regeneration() {}
		public Regeneration(Decoder in) { super(in); }
	}

	@RegisterSerializable("BuffStun")
	public static final class Stun extends Buff {
		public Stun() {}
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

		public Tooltip() {}

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