package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandPlayerSetEquipment")
public final class PlayerSetEquipmentStatement extends StatementNode {

	public EquipmentSlot slot;
	public UUID itemId;

	public PlayerSetEquipmentStatement() {}

	public PlayerSetEquipmentStatement(Decoder in) {
		slot = in.readEnum("EquipSlot", EquipmentSlot.class);
		itemId = in.readUUID("ItemGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeEnum("EquipSlot", slot);
		to.writeUUID("ItemGuid", itemId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetEquipmentStatement a)) return false;
		return slot == a.slot && Objects.equals(itemId, a.itemId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(slot, itemId);
	}

	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum EquipmentSlot implements INamedEnum {

		WEAPON("Weapon"),
		ARMOR("Armor"),
		ACCESSORY_1("Accessory1"),
		ACCESSORY_2("Accessory2");

		private final String id;

		private EquipmentSlot(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}