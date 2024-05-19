package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Handles setup and display of <a href="https://docs.finmer.dev/script-reference/shop">shops</a>.
 */
@RegisterSerializable("CommandShop")
public final class ShopStatement extends StatementNode {

	/**
	 * The unique "internal" name of the shop.
	 * This is the name that the shop's save data is associated with.
	 */
	public String key;

	/**
	 * The name of the shop that is presented to the player.
	 */
	public String title;

	/**
	 * The interval, in hours, between restocks of the shop.
	 */
	public int restockInterval = 12;

	/**
	 * A {@code Map} containing the various things on sale.
	 */
	public Map<UUID, Integer> merchandise = new LinkedHashMap<>();

	/**
	 * Constructs a new {@code ShopStatement} with default values.
	 */
	public ShopStatement() {}

	/**
	 * Decodes a {@code ShopStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public ShopStatement(Decoder in) {
		key = in.readString("Key");
		title = in.readString("Title");
		restockInterval = in.readInt("RestockInterval");

		in.readList("Merchandise", dec -> new EphemeralItem(dec.readUUID("Item"), dec.readInt("Quantity")))
		.forEach(i -> merchandise.put(i.itemId, i.quantity));
	}

	@Override
	public void write(Encoder to) {
		to.writeString("Key", key);
		to.writeString("Title", title);
		to.writeInt("RestockInterval", restockInterval);

		// Make your serialization code 2x slower with this one neat trick!
		to.writeList("Merchandise", merchandise.entrySet()
				.stream()
				.map(e -> new EphemeralItem(e.getKey(), e.getValue()))
				.toList(), (i, enc) -> {
					enc.writeUUID("Item", i.itemId);
					enc.writeInt("Quantity", i.quantity);
				});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ShopStatement a)) return false;
		return Objects.equals(key, a.key) && Objects.equals(title, a.title)
				&& restockInterval == a.restockInterval && Objects.equals(merchandise, a.merchandise);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, title, restockInterval, merchandise);
	}

	private static record EphemeralItem(UUID itemId, int quantity) {}
}