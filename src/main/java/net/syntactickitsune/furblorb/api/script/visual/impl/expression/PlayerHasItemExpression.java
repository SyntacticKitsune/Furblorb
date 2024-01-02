package net.syntactickitsune.furblorb.api.script.visual.impl.expression;

import java.util.Objects;
import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.expression.ExpressionNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("ConditionPlayerHasItem")
public final class PlayerHasItemExpression extends ExpressionNode {

	public UUID itemId;

	public PlayerHasItemExpression() {}

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