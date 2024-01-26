package net.syntactickitsune.furblorb.api.asset;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * A {@code StringTableAsset} represents well, a table (or {@link Map}) of strings.
 * More specifically, a {@code StringTableAsset} contains a series of strings that are mapped to lists of strings that are chosen randomly from.
 * See also <a href="https://docs.finmer.dev/asset-types/string-tables">the documentation</a>.
 */
@RegisterSerializable("AssetStringTable")
public final class StringTableAsset extends FurballAsset {

	public final Map<String, List<String>> table = new LinkedHashMap<>();

	public StringTableAsset() {}

	public StringTableAsset(Decoder in) {
		super(in);

		final List<Entry> entries = in.readList("Entries", Entry::new);

		for (Entry entry : entries)
			table.put(entry.key, entry.values);
	}

	@Override
	protected void write0(Encoder to) {
		// So we're supposed to be sorting the table first,
		// but I think instead we'll just go with the
		// determinism that comes with preserved insertion order.

		final List<Entry> entries = table.entrySet()
				.stream()
				.map(Entry::new)
				.toList();

		to.writeList("Entries", entries, Entry::write);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof StringTableAsset a)) return false;
		return id.equals(a.id) && Objects.equals(filename, a.filename)
				&& Objects.equals(table, a.table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, filename, table);
	}

	private static record Entry(String key, List<String> values) {

		private Entry {}

		private Entry(Map.Entry<String, List<String>> entry) {
			this(entry.getKey(), entry.getValue());
		}

		private Entry(Decoder in) {
			this(in.readString("Key"), in.readStringList("Text"));
		}

		private void write(Encoder to) {
			to.writeString("Key", key);
			to.writeStringList("Text", values);
		}
	}
}