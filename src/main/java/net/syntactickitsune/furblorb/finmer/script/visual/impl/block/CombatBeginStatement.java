package net.syntactickitsune.furblorb.finmer.script.visual.impl.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.finmer.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * Begins combat with a specified configuration.
 */
@RegisterSerializable("CommandCombatBegin")
public final class CombatBeginStatement extends StatementBlockNode {

	/**
	 * Whether to include the player in the combat.
	 */
	public boolean includePlayer;

	/**
	 * The NPC participants to include in the combat.
	 */
	public final List<Participant> participants = new ArrayList<>();

	/**
	 * A series of statements to run when the combat begins.
	 */
	@Nullable
	public List<ScriptNode> onStart;

	/**
	 * <p>
	 * A series of statements to run any time a round of combat begins.
	 * </p>
	 * <p>
	 * Only available from format version 20 (Finmer v1.0.1) onwards.
	 * </p>
	 */
	@Nullable
	@RequiresFormatVersion(20)
	public List<ScriptNode> onRoundStart;

	/**
	 * A series of statements to run any time a round of combat ends.
	 */
	@Nullable
	public List<ScriptNode> onRoundEnd;

	/**
	 * A series of statements to run when the player is killed.
	 */
	@Nullable
	public List<ScriptNode> onPlayerKilled;

	/**
	 * A series of statements to run when an NPC is killed.
	 */
	@Nullable
	public List<ScriptNode> onCreatureKilled;

	/**
	 * A series of statements to run when an NPC is consumed.
	 */
	@Nullable
	public List<ScriptNode> onCreatureVored;

	/**
	 * A series of statements to run when an NPC is released.
	 */
	@Nullable
	public List<ScriptNode> onCreatureReleased;

	/**
	 * Constructs a new {@code CombatBeginStatement} with default values.
	 */
	public CombatBeginStatement() {}

	/**
	 * Decodes a {@code CombatBeginStatement} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public CombatBeginStatement(Decoder in) {
		includePlayer = in.readBoolean("IncludePlayer");
		in.readBoolean("IncludeAllies");

		participants.addAll(in.readObjectList("Participants", dec -> new Participant(dec.readString("Variable"), dec.readUUID("Creature"), dec.readBoolean("IsAlly"))));

		onStart = readOptional("CallbackCombatStart", in);
		if (in.formatVersion() >= 20)
			onRoundStart = readOptional("CallbackRoundStart", in);
		onRoundEnd = readOptional("CallbackRoundEnd", in);
		onPlayerKilled = readOptional("CallbackPlayerKilled", in);
		onCreatureKilled = readOptional("CallbackCreatureKilled", in);
		onCreatureVored = readOptional("CallbackCreatureVored", in);
		onCreatureReleased = readOptional("CallbackCreatureReleased", in);
	}

	@Override
	public void write(Encoder to) {
		to.writeBoolean("IncludePlayer", includePlayer);
		to.writeBoolean("IncludeAllies", includePlayer); // Reserved, apparently. Why not just increment the format version?

		to.writeObjectList("Participants", participants, (p, enc) -> {
			enc.writeString("Variable", p.id);
			enc.writeUUID("Creature", p.creature);
			enc.writeBoolean("IsAlly", p.ally);
		});

		writeOptional("CallbackCombatStart", onStart, to);
		if (to.formatVersion() >= 20)
			writeOptional("CallbackRoundStart", onRoundStart, to);
		writeOptional("CallbackRoundEnd", onRoundEnd, to);
		writeOptional("CallbackPlayerKilled", onPlayerKilled, to);
		writeOptional("CallbackCreatureKilled", onCreatureKilled, to);
		writeOptional("CallbackCreatureVored", onCreatureVored, to);
		writeOptional("CallbackCreatureReleased", onCreatureReleased, to);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitVisualCode(this)) {
			if (onStart != null)
				for (ScriptNode sn : onStart)
					sn.visit(visitor);
			if (onRoundStart != null)
				for (ScriptNode sn : onRoundStart)
					sn.visit(visitor);
			if (onRoundEnd != null)
				for (ScriptNode sn : onRoundEnd)
					sn.visit(visitor);
			if (onPlayerKilled != null)
				for (ScriptNode sn : onPlayerKilled)
					sn.visit(visitor);
			if (onCreatureKilled != null)
				for (ScriptNode sn : onCreatureKilled)
					sn.visit(visitor);
			if (onCreatureVored != null)
				for (ScriptNode sn : onCreatureVored)
					sn.visit(visitor);
			if (onCreatureReleased != null)
				for (ScriptNode sn : onCreatureReleased)
					sn.visit(visitor);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CombatBeginStatement a)) return false;
		return includePlayer == a.includePlayer && Objects.equals(participants, a.participants)
				&& Objects.equals(onStart, a.onStart) && Objects.equals(onRoundStart, a.onRoundStart)
				&& Objects.equals(onRoundEnd, a.onRoundEnd) && Objects.equals(onPlayerKilled, a.onPlayerKilled)
				&& Objects.equals(onCreatureKilled, a.onCreatureKilled) && Objects.equals(onCreatureVored, a.onCreatureVored)
				&& Objects.equals(onCreatureReleased, a.onCreatureReleased);
	}

	@Override
	public int hashCode() {
		return Objects.hash(includePlayer, participants, onStart, onRoundStart, onRoundEnd,
				onPlayerKilled, onCreatureKilled, onCreatureVored, onCreatureReleased);
	}

	@Nullable
	private static List<ScriptNode> readOptional(String key, Decoder in) {
		return in.readBoolean("Has" + key) ? read(key, in) : null;
	}

	private static void writeOptional(String key, @Nullable List<ScriptNode> list, Encoder out) {
		if (list == null)
			out.writeBoolean("Has" + key, false);
		else {
			out.writeBoolean("Has" + key, true);
			write(key, list, out);
		}
	}

	/**
	 * Represents an NPC participant in combat.
	 * @param id The participant ID. Used to identify the participant in combat.
	 * @param creature The asset ID of the participant.
	 * @param ally Whether the participant is an ally of the player versus an enemy.
	 */
	public static record Participant(String id, UUID creature, boolean ally) {}
}