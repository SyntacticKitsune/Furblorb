package net.syntactickitsune.furblorb.cli;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.AssetShufflerRegistry;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.FurballDependency;
import net.syntactickitsune.furblorb.finmer.asset.CreatureAsset;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.finmer.asset.ItemAsset;
import net.syntactickitsune.furblorb.finmer.asset.JournalAsset;
import net.syntactickitsune.furblorb.finmer.asset.SceneAsset;
import net.syntactickitsune.furblorb.finmer.asset.ScriptAsset;
import net.syntactickitsune.furblorb.finmer.asset.StringTableAsset;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.finmer.io.FurballReader;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.FurballWriter;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ReadOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter.WriteOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;
import net.syntactickitsune.furblorb.io.codec.JsonCodec;

/**
 * A container class for all of the {@link Step} implementations.
 * @author SyntacticKitsune
 */
final class Steps {

	static final record Read(Path from) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final String kind;

			final String filename = from.getFileName().toString();

			try {
				if (filename.endsWith(".fnproj")) {
					data.furball = new FinmerProjectReader(ReadOnlyExternalFileHandler.forProjectFile(from)).readFurball();
					kind = "Finmer project";
				} else if (filename.endsWith(".furball")) {
					data.furball = new FurballReader(Files.readAllBytes(from)).readFurball();
					kind = "furball";
				} else
					throw new CliException("don't know how to read from " + filename + ", it does not seem to be a furball (.furball) or project (.fnproj)");
			} catch (NoSuchFileException e) {
				throw new CliException("file \"" + e.getFile() + "\" does not exist; cannot read it");
			} catch (AccessDeniedException e) {
				throw new CliException("could not read file \"" + e.getFile() + "\": access denied");
			}

			System.out.printf("! Read %s \"%s\" by %s with %d assets (format version %d).\n", kind,
					data.furball.meta.title, data.furball.meta.author, data.furball.assets.size(), data.furball.meta.formatVersion);

			check(data);
		}

		private void check(WorkingData data) {
			final Map<UUID, String> assetsById = new HashMap<>();

			for (FurballAsset asset : data.furball.assets) {
				if (assetsById.containsValue(asset.filename))
					System.out.printf("! Warning: multiple assets with name %s: %s and %s\n", asset.filename,
							assetsById.entrySet().stream()
							.filter(entry -> asset.filename.equals(entry.getValue()))
							.map(Map.Entry::getKey)
							.map(Object::toString)
							.collect(Collectors.joining(", ")),
							asset.id);

				final String name = assetsById.putIfAbsent(asset.id, asset.filename);
				if (name != null)
					System.out.printf("! Warning: multiple assets with id %s: %s and %s\n", asset.id, name, asset.filename);
			}
		}
	}

	static final record Write(Path to) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no loaded furball to write");

			if (data.formatVersion != null && data.formatVersion != furball.meta.formatVersion) {
				if (data.formatVersion > furball.meta.formatVersion)
					System.out.println("! Upgrading format version to " + data.formatVersion + " (up from " + furball.meta.formatVersion + ").");
				else
					System.out.println("! Downgrading format version to " + data.formatVersion + " (down from " + furball.meta.formatVersion + ").");

				furball.meta.formatVersion = data.formatVersion;
			}

			final String kind;

			final String filename = to.getFileName().toString();

			try {
				if (filename.endsWith(".furball")) {
					final BinaryCodec codec = new BinaryCodec(CodecMode.WRITE_ONLY);
					new FurballWriter(codec).write(furball);

					Files.write(to, codec.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

					kind = "furball";
				} else if (filename.endsWith(".fnproj")) {
					new FinmerProjectWriter(WriteOnlyExternalFileHandler.forProjectFile(to)).writeFurball(furball);
					kind = "Finmer project";
				} else
					throw new CliException("don't know how to write to " + filename + ", it does not seem to be a furball (.furball) or project (.fnproj)");
			} catch (AccessDeniedException e) {
				throw new CliException("could not write to file \"" + e.getFile() + "\": access denied");
			}

			System.out.printf("! Completed: wrote a %s to %s\n", kind, to().toAbsolutePath());
		}
	}

	static final record AdjustFormatVersion(byte target) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			data.formatVersion = target;
		}
	}

	static final record ChangeTitle(String value) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to change the title of");
			System.out.printf("! Changed title %s => %s.", furball.meta.title, value);
			furball.meta.title = value;
		}
	}

	static final record ChangeAuthor(String value) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to change the author of");
			System.out.printf("! Changed author %s => %s.", data.furball.meta.author, value);
			furball.meta.author = value;
		}
	}

	static final record AddDependency(UUID id, String filename) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to add dependencies to");
			furball.dependencies.removeIf(dep -> dep.id().equals(id));
			furball.dependencies.add(new FurballDependency(id, filename));
			System.out.printf("! Added dependency %s (%s).", id, filename);
		}
	}

	static final record DropDependency(UUID id) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to drop a dependency from");
			if (furball.dependencies.removeIf(dep -> dep.id().equals(id)))
				System.out.printf("! Dropped dependency %s.", id);
		}
	}

	static final record InsertAsset(Path assetPath) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to insert an asset into");
			System.out.printf("! Attempting to insert asset %s.\n", assetPath.toAbsolutePath());

			String json = Files.readString(assetPath);
			if (json.charAt(0) == 65279) json = json.substring(1); // Remove the BOM, if present.

			final JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
			final JsonCodec codec = new JsonCodec(obj, new ReadOnlyExternalFileHandler(assetPath.getParent(), assetPath), CodecMode.READ_ONLY, data.formatVersion());
			final FurballAsset asset = FurballSerializables.read(codec);

			System.out.printf("! Inserted asset %s (%s).\n", asset.filename, asset.id);

			furball.assets.add(asset);
		}
	}

	static final record ExtractAsset(@Nullable String filename, @Nullable UUID id, Path dest) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to extract an asset from");
			for (FurballAsset asset : furball.assets)
				if ((filename != null && asset.filename.equals(filename)) || (id != null && asset.id.equals(id))) {
					System.out.printf("! Extracting asset %s (%s) to %s.\n", asset.filename, asset.id, dest.toAbsolutePath());
					{
						final JsonCodec codec = new JsonCodec(new WriteOnlyExternalFileHandler(dest.getParent(), dest), data.formatVersion());
						asset.writeWithId(codec);
						Files.writeString(dest, FinmerProjectWriter.toJson(codec.unwrap()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
					}
					break;
				}
		}
	}

	static final record DropAsset(@Nullable String filename, @Nullable UUID id) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to drop an asset from");
			for (Iterator<FurballAsset> it = furball.assets.iterator(); it.hasNext(); ) {
				final FurballAsset asset = it.next();
				if ((filename != null && asset.filename.equals(filename)) || (id != null && asset.id.equals(id))) {
					System.out.printf("! Dropped asset %s (%s).\n", asset.filename, asset.id);
					it.remove();
				}
			}
		}
	}

	static final record SortAssets() implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to sort the assets of");
			Collections.sort(furball.assets);
			System.out.println("! Sorted all assets.");
		}
	}

	static final record Merge(Path other) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to merge another furball with");
			System.out.printf("! Attempting to merge %s...\n", other);

			final WorkingData newData = new WorkingData();
			new Read(other).run(newData);
			final Furball merging = newData.furball;

			// Start with dependencies:
			int mergedDeps = 0;
			for (FurballDependency dep : merging.dependencies)
				if (!dep.id().equals(furball.meta.id) && !furball.dependencies.contains(dep)) {
					furball.dependencies.add(dep);
					mergedDeps++;
				}

			parent:
			for (FurballAsset ours : merging.assets) {
				for (ListIterator<FurballAsset> it = furball.assets.listIterator(); it.hasNext(); ) {
					final FurballAsset theirs = it.next();
					if (ours.id.equals(theirs.id)) {
						it.set(ours);
						continue parent;
					}
				}

				furball.assets.add(ours);
			}

			System.out.printf("! Merged %d dependenc%s and %d asset%s from %s (%s) into %s (%s).\n",
					mergedDeps, mergedDeps == 1 ? "y" : "ies", merging.assets.size(), merging.assets.size() == 1 ? "" : "s",
					merging.meta.title, merging.meta.id, furball.meta.title, furball.meta.id);
		}
	}

	static final record Show(boolean verbose) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to show metadata of");
			System.out.println("\n! Furball metadata:");
			System.out.printf("ID:               %s\n", furball.meta.id);
			System.out.printf("Format Version:   %d\n", furball.meta.formatVersion);
			System.out.printf("Title:            %s\n", furball.meta.title);
			System.out.printf("Author:           %s\n", furball.meta.author);

			if (furball.dependencies.isEmpty())
				System.out.println("\n! No dependencies.");
			else {
				System.out.printf("\n! Dependencies (%d):\n", furball.dependencies.size());
				for (FurballDependency dep : furball.dependencies)
					System.out.printf("- %s (%s)", dep.filename(), dep.id());
			}

			if (furball.assets.isEmpty())
				System.out.println("\n! No assets.");
			else {
				System.out.println("\n! Asset summary:");
				System.out.printf("Total:            %d\n", furball.assets.size());
				System.out.printf("Scenes:           %s\n", count(furball, SceneAsset.class));
				System.out.printf("Creatures:        %s\n", count(furball, CreatureAsset.class));
				System.out.printf("Items:            %s\n", count(furball, ItemAsset.class));
				System.out.printf("String Tables:    %s\n", count(furball, StringTableAsset.class));
				System.out.printf("Journals:         %s\n", count(furball, JournalAsset.class));
				System.out.printf("Scripts:          %s\n", count(furball, ScriptAsset.class));

				if (verbose) {
					final int nameWidth = Math.max(furball.assets.stream()
							.map(fa -> fa.filename.length() + 2)
							.reduce(0, Math::max), 12);

					System.out.printf("\n! Assets (%d):\n", furball.assets.size());
					System.out.printf("! Type%sFile Name%sID\n", " ".repeat(16 - 4), " ".repeat(nameWidth - 9));
					for (FurballAsset asset : furball.assets)
						// Concatenation? In *MY* format strings? It's more likely than you think.
						System.out.printf(
								"- %-16s%-" + nameWidth + "s%s\n",
								asset.metadata().name().substring("Asset".length()),
								asset.filename,
								asset.id);
				}
			}
		}

		private static String count(Furball furball, Class<? extends FurballAsset> clazz) {
			final long count = furball.assets.stream().filter(clazz::isInstance).count();
			final int fullCount = furball.assets.size();
			return "%d (%.1f%%)".formatted(count, ((double) count / fullCount * 100));
		}
	}

	static final record ListShufflers() implements Step {
		@Override
		public void run(WorkingData data) {
			final var registry = AssetShufflerRegistry.getRegistry();

			System.out.printf("! There are %d registered shufflers:\n", registry.size());

			for (Map.Entry<String, AssetShuffler<?>> entry : registry.entrySet())
				System.out.printf("%s: %s\n", entry.getKey(), entry.getValue().description());

			System.out.printf("! To apply all of them: --shuffle %s (or use --shuffle all)\n", String.join(",", registry.keySet()));
		}
	}

	static final record Shuffle(String keys, @Nullable String seed) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to shuffle");

			final List<String> realKeys;
			if ("all".equals(keys))
				realKeys = List.copyOf(AssetShufflerRegistry.getRegistry().keySet());
			else
				realKeys = List.of(keys.split(","));

			final Function<RandomGeneratorFactory<?>, RandomGenerator> func;

			if (seed != null)
				func = factory -> {
					long seed;

					try {
						seed = Long.parseLong(this.seed);
					} catch (NumberFormatException ignored) {
						seed = this.seed.hashCode();
					}

					return factory.create(seed);
				};
			else
				func = RandomGeneratorFactory::create;

			final ShuffleRandom random = ShuffleRandom.of(func.apply(RandomGeneratorFactory.of("Xoshiro256PlusPlus")));
			final Map<Class<?>, List<?>> assetCache = new HashMap<>();

			for (String key : realKeys) {
				final AssetShuffler shuffler = AssetShufflerRegistry.get(key);

				if (shuffler == null) {
					System.out.println("! No known shuffler with key " + key + ", skipping.");
					continue;
				}

				final List<?> assets = assetCache.computeIfAbsent(shuffler.assetType(),
						k -> furball.assets.stream()
								.filter(asset -> k.isAssignableFrom(asset.getClass()))
								.toList());

				System.out.println("! Shuffling " + assets.size() + " assets using shuffler " + key + ".");

				shuffler.shuffle(assets, random, furball);
			}

			System.out.println("! Shuffling completed.");
		}
	}
}