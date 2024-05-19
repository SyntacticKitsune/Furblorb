package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * From <a href="https://docs.finmer.dev/script-reference/player">the documentation</a>:
 * "Returns true if the player has an item in their inventory (<i>not</i> equipment!) with the specified asset name."
 */
@RegisterSerializable("ConditionPlayerHasItem")
public final class PlayerHasItemExpression extends ExpressionNode {

	/**
	 * The asset ID of the item in question.
	 */
	public UUID itemId;

	/**
	 * Constructs a new {@code PlayerHasItemExpression} with default values.
	 */
	public PlayerHasItemExpression() {}

	/**
	 * Decodes a {@code PlayerHasItemExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerHasItemExpression(Decoder in) {
		itemId = in.readUUID("ItemGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("ItemGuid", itemId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerHasItemExpression a)) return false;
		return Objects.equals(itemId, a.itemId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(itemId);
	}
}