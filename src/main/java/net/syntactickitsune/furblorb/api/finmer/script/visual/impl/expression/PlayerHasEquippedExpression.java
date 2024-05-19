package net.syntactickitsune.furblorb.api.finmer.script.visual.impl.expression;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.api.finmer.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;

/**
 * Checks whether the player has a specified item equipped.
 */
@RegisterSerializable("ConditionPlayerEquipment")
public final class PlayerHasEquippedExpression extends ExpressionNode {

	/**
	 * The asset ID of the item in question.
	 */
	public UUID itemId;

	/**
	 * Constructs a new {@code PlayerHasEquippedExpression} with default values.
	 */
	public PlayerHasEquippedExpression() {}

	/**
	 * Decodes a {@code PlayerHasEquippedExpression} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public PlayerHasEquippedExpression(Decoder in) {
		itemId = in.readUUID("ItemGuid");
	}

	@Override
	public void write(Encoder to) {
		to.writeUUID("ItemGuid", itemId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof PlayerHasEquippedExpression a)) return false;
		return Objects.equals(itemId, a.itemId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(itemId);
	}
}