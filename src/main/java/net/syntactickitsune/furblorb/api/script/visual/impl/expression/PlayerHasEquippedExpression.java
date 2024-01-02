package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerEquipment")
public final class PlayerHasEquippedExpression extends ExpressionNode {

	public UUID itemId;

	public PlayerHasEquippedExpression() {}

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