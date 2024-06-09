package net.syntactickitsune.furblorb.finmer.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;

/**
 * A {@code JournalAsset} describes a "journal" with multiple stages of progress.
 * In other words: it describes the stages of a quest-line.
 * See also <a href="https://docs.finmer.dev/asset-types/journals">the documentation</a>.
 */
@RegisterSerializable("AssetJournal")
public final class JournalAsset extends FurballAsset {

	/**
	 * The title of the journal.
	 */
	public String title;

	/**
	 * The list of stages or states that the journal can be in.
	 */
	public final List<Stage> stages = new ArrayList<>();

	/**
	 * Constructs a new {@code JournalAsset} with default values.
	 */
	public JournalAsset() {}

	/**
	 * Decodes a {@code JournalAsset} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public JournalAsset(Decoder in) {
		super(in);
		title = in.readString("Title");
		stages.addAll(in.readObjectList("Stages", Stage::new));
	}

	@Override
	protected void write0(Encoder to) {
		to.writeString("Title", title);
		to.writeObjectList("Stages", stages, Stage::write);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitAsset(this)) {
			visitor.visitText(title);
			for (Stage stage : stages)
				visitor.visitText(stage.text);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof JournalAsset a)) return false;
		return id.equals(a.id) && Objects.equals(filename, a.filename)
				&& Objects.equals(title, a.title) && Objects.equals(stages, a.stages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, filename, title, stages);
	}

	/**
	 * Represents an individual stage of a journal.
	 * @param key The numeric identifier of the stage.
	 * @param text The text describing the stage.
	 */
	public static record Stage(int key, String text) {

		/**
		 * Constructs a {@code Stage} with the specified values.
		 * @param key The numeric identifier of the stage.
		 * @param text The text describing the stage.
		 */
		public Stage {}

		/**
		 * Decodes a {@code Stage} from the specified {@code Decoder}.
		 * @param in The {@code Decoder}.
		 * @throws NullPointerException If {@code in} is {@code null}.
		 */
		public Stage(Decoder in) {
			this(in.readInt("Key"), in.readString("Text"));
		}

		/**
		 * Writes this {@code Stage} to the specified {@code Encoder}.
		 * @param to The {@code Encoder}.
		 * @throws NullPointerException If {@code to} is {@code null}.
		 */
		public void write(Encoder to) {
			to.writeInt("Key", key);
			to.writeString("Text", text);
		}
	}
}