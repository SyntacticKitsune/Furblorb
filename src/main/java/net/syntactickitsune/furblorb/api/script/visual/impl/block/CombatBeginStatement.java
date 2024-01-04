package net.syntactickitsune.furblorb.api.script.visual.impl.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.RequiresFormatVersion;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.script.visual.ScriptNode;
import net.syntactickitsune.furblorb.api.script.visual.StatementBlockNode;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

@RegisterSerializable("CommandCombatBegin")
public final class CombatBeginStatement extends StatementBlockNode {

	public boolean includePlayer;
	public final List<Participant> participants = new ArrayList<>();
	@Nullable
	public List<ScriptNode> onStart;
	@Nullable
	@RequiresFormatVersion(20)
	public List<ScriptNode> onRoundStart;
	@Nullable
	public List<ScriptNode> onRoundEnd;
	@Nullable
	public List<ScriptNode> onPlayerKilled;
	@Nullable
	public List<ScriptNode> onCreatureKilled;
	@Nullable
	public List<ScriptNode> onCreatureVored;
	@Nullable
	public List<ScriptNode> onCreatureReleased;

	public CombatBeginStatement() {}

	public CombatBeginStatement(Decoder in) {
		includePlayer = in.readBoolean("IncludePlayer");
		in.readBoolean("IncludeAllies");

		participants.addAll(in.readList("Participants", dec -> new Participant(dec.readString("Variable"), dec.readUUID("Creature"), dec.readBoolean("IsAlly"))));

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

		to.writeList("Participants", participants, (p, enc) -> {
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

	public static record Participant(String id, UUID creature, boolean ally) {}
}