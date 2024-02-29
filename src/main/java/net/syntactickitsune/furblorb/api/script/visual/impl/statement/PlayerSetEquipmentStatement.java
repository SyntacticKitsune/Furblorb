package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.io.ParsingStrategy;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Sets the equipment in a specified equipment slot of the player.
 */
@RegisterSerializable("CommandPlayerSetEquipment")
public final class PlayerSetEquipmentStatement extends StatementNode {

	/**
	 * The equipment slot to change.
	 */
	public EquipmentSlot slot;

	/**
	 * The asset ID of the item to assign to the slot.
	 */
	public UUID itemId;

	/**
	 * Constructs a new {@code PlayerSetEquipmentStatement} with default values.
	 */
	public PlayerSetEquipmentStatement() {}

	/**
	 * Decodes a {@code PlayerSetEquipmentStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
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

	/**
	 * Represents the available equipment slots.
	 * This is different from {@link net.syntactickitsune.furblorb.api.asset.ItemAsset.EquipmentSlot} because this one disambiguates between the two accessory slots.
	 */
	@ParsingStrategy(ParsingStrategy.NumberType.INT)
	public static enum EquipmentSlot implements INamedEnum {

		/**
		 * The weapon equipment slot.
		 */
		WEAPON("Weapon"),

		/**
		 * The armor equipment slot.
		 */
		ARMOR("Armor"),

		/**
		 * The first accessory equipment slot.
		 */
		ACCESSORY_1("Accessory1"),

		/**
		 * The second accessory equipment slot.
		 */
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