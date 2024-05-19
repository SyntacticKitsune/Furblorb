package net.syntactickitsune.furblorb.finmer.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Adds or removes a specified item from the player's inventory.
 */
@RegisterSerializable("CommandPlayerSetItem")
public final class PlayerSetItemStatement extends StatementNode {

	/**
	 * The asset ID of the item.
	 */
	public UUID itemId;

	/**
	 * Whether to add the item instead of removing it.
	 */
	public boolean add = true;

	/**
	 * Whether addition of the item should be performed "quietly."
	 */
	public boolean quiet;

	/**
	 * Constructs a new {@code PlayerSetItemStatement} with default values.
	 */
	public PlayerSetItemStatement() {}

	/**
	 * Decodes a {@code PlayerSetItemStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerSetItemStatement(Decoder in) {
		itemId = in.readUUID("ItemGuid");
		add = in.readBoolean("Add");
		if (add)
			quiet = in.readBoolean("Quiet");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("ItemGuid", itemId);
		to.writeBoolean("Add", add);
		if (add)
			to.writeBoolean("Quiet", quiet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerSetItemStatement a)) return false;
		return add == a.add && quiet == a.quiet && Objects.equals(itemId, a.itemId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, add, quiet);
	}
}