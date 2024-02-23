package net.syntactickitsune.furblorb.api.script.visual.impl.statement;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.StatementNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

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

	public PlayerSetItemStatement() {}

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