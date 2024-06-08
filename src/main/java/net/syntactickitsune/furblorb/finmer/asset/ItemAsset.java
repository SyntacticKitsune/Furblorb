package net.syntactickitsune.furblorb.finmer.asset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.component.buff.EquipEffectGroup;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;

/**
 * An {@code ItemAsset} defines the initial attributes of an item.
 * See also <a href="https://docs.finmer.dev/asset-types/items">the documentation</a>.
 */
@RegisterSerializable("AssetItem")
public final class ItemAsset extends FurballAsset {

	/**
	 * The name of the item.
	 */
	public String objectName = "";

	/**
	 * An alternative name for the item.
	 */
	public String objectAlias = "";

	/**
	 * The "flavor text" of the item.
	 * This is also called a description or tooltip in other games.
	 */
	public String flavorText = ""; // This is what we here in the business call a "description."

	/**
	 * The type of item.
	 */
	public Type type;

	/**
	 * For equippable items, this is the slot they go in.
	 */
	public EquipmentSlot slot;

	/**
	 * For equippable items, this is the list of effects they have.
	 */
	public final List<EquipEffectGroup> equipEffects = new ArrayList<>();

	/**
	 * The price of the item.
	 * A price of 0 means the item cannot be sold.
	 */
	public int price;

	/**
	 * Whether or not the item is a quest item.
	 */
	public boolean questItem = false;

	/**
	 * For usable items, whether or not the item is consumable.
	 * This determines whether or not the item should be removed from the player's inventory after use.
	 */
	public boolean consumable = false;

	/**
	 * For usable items, whether they can be used outside of battle.
	 */
	public boolean usableInField = false;

	/**
	 * For usable items, whether they can be used inside battle.
	 */
	public boolean usableInBattle = false;

	/**
	 * For usable items, a description of what the item does.
	 */
	public String useDescription = "";

	/**
	 * An array containing the raw bytes of the item's icon in PNG format.
	 * May be {@code null}, which indicates that the item lacks an icon.
	 */
	public byte @Nullable [] icon; // Sorry, we're storing PNGs now???

	/**
	 * For usable items, a script to run when the item is used.
	 */
	public ScriptAsset useScript;

	/**
	 * Constructs a new {@code ItemAsset} with default values.
	 */
	public ItemAsset() {}

	/**
	 * Decodes an {@code ItemAsset} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ItemAsset(Decoder in) {
		super(in);
		objectName = in.readString("ObjectName");
		objectAlias = in.readString("ObjectAlias");
		flavorText = in.readString("FlavorText");
		type = in.readEnum("ItemType", Type.class);
		if (type == Type.EQUIPPABLE) {
			slot = in.readEnum("EquipSlot", EquipmentSlot.class);
			equipEffects.addAll(in.readOptionalObjectList("EquipEffects", FurballSerializables::read));
		} else {
			in.assertDoesNotExist("EquipSlot", "Only equippable items may have equipment slots");
			in.assertDoesNotExist("EquipEffects", "Only equippable items may have effects");
		}

		price = in.readInt("PurchaseValue");

		if (in.validate() && price < 0)
			throw new FurblorbParsingException("Price value out of range [0,∞): " + price);

		questItem = in.readBoolean("IsQuestItem");

		if (type == Type.USABLE) {
			consumable = in.readBoolean("IsConsumable");
			usableInField = in.readBoolean("CanUseInField");
			usableInBattle = in.readBoolean("CanUseInBattle");
			useDescription = in.readString("UseDescription");
			useScript = in.readOptionalObject("UseScript", FurballSerializables::read);
		} else {
			in.assertDoesNotExist("IsConsumable", "Only usable items may be consumable");
			in.assertDoesNotExist("CanUseInField", "Only usable items may be used in field");
			in.assertDoesNotExist("CanUseInBattle", "Only usable items may be used in battle");
			in.assertDoesNotExist("UseDescription", "Only usable items may have use descriptions");
			in.assertDoesNotExist("UseScript", "Only usable items may have use scripts");
		}

		icon = in.readExternal(filename + ".png", Decoder::readByteArray, Function.identity());
	}

	@Override
	protected void write0(Encoder to) {
		if (to.validate() && price < 0)
			throw new FurblorbParsingException("Price value out of range [0,∞): " + price);

		to.writeString("ObjectName", objectName);
		to.writeString("ObjectAlias", objectAlias);
		to.writeString("FlavorText", flavorText);
		to.writeEnum("ItemType", type);

		if (type == Type.EQUIPPABLE) {
			to.writeEnum("EquipSlot", slot);
			to.writeOptionalObjectList("EquipEffects", equipEffects, EquipEffectGroup::writeWithId);
		} else {
			to.assertDoesNotExist("EquipSlot", slot, "Only equippable items may have equipment slots");
			to.assertDoesNotExist("EquipEffects", equipEffects.isEmpty() ? null : equipEffects, "Only equippable items may have effects");
		}

		to.writeInt("PurchaseValue", price);
		to.writeBoolean("IsQuestItem", questItem);

		if (type == Type.USABLE) {
			to.writeBoolean("IsConsumable", consumable);
			to.writeBoolean("CanUseInField", usableInField);
			to.writeBoolean("CanUseInBattle", usableInBattle);
			to.writeString("UseDescription", useDescription);
			to.writeOptionalObject("UseScript", useScript, ScriptAsset::writeWithId);
		} else if (to.validate()) {
			to.assertDoesNotExist("UseDescription", useDescription.isEmpty() ? null : "", "Only usable items may have use descriptions");
			to.assertDoesNotExist("UseScript", useScript, "Only usable items may have use scripts");
		}

		to.writeExternal(filename + ".png", icon, (key, v, enc) -> enc.writeByteArray(key, v), Function.identity());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ItemAsset a)) return false;
		return id.equals(a.id) && Objects.equals(filename, a.filename)
				&& usableInBattle == a.usableInBattle && usableInField == a.usableInField
				&& consumable == a.consumable && questItem == a.questItem && price == a.price
				&& slot == a.slot && type == a.type && Objects.equals(useDescription, a.useDescription)
				&& Objects.equals(flavorText, a.flavorText) && Objects.equals(objectAlias, a.objectAlias)
				&& Objects.equals(objectName, a.objectName) && Arrays.equals(icon, a.icon) && Objects.equals(useScript, a.useScript)
				&& Objects.equals(equipEffects, a.equipEffects);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(new Object[] {
				id, filename, objectName, objectAlias, flavorText, type, slot, equipEffects,
				price, questItem, consumable, usableInField, usableInBattle, useDescription, icon,
				useScript
		});
	}

	/**
	 * Represents the different types of items.
	 */
	public static enum Type implements INamedEnum {

		/**
		 * Represents items that have no specific gameplay use.
		 * These would be fetch quest items, collectibles, perfectly generic items, etc.
		 */
		GENERIC("Generic"), // Like me!

		/**
		 * Represents items that can be equipped.
		 */
		EQUIPPABLE("Equipable"),

		/**
		 * Represents items that can be "used", such as consumables.
		 */
		USABLE("Usable");

		private final String id;

		private Type(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}

	/**
	 * Represents the different equipment slots an item may occupy.
	 */
	public static enum EquipmentSlot implements INamedEnum {

		/**
		 * The item occupies the weapon slot.
		 */
		WEAPON("Weapon"),

		/**
		 * The item occupies the armor slot.
		 */
		ARMOR("Armor"),

		/**
		 * The item occupies one of two accessory slots.
		 */
		ACCESSORY("Accessory");

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