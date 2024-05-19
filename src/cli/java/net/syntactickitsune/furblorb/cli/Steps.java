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

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballDependency;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.io.FinmerProjectReader;
import net.syntactickitsune.furblorb.api.io.FinmerProjectReader.ReadOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.api.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.api.io.FinmerProjectWriter.WriteOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.api.io.FurballReader;
import net.syntactickitsune.furblorb.api.io.FurballWriter;
import net.syntactickitsune.furblorb.api.io.impl.BinaryCodec;
import net.syntactickitsune.furblorb.api.io.impl.JsonCodec;
import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.AssetShufflerRegistry;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.io.FurballSerializables;

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
					final BinaryCodec codec = new BinaryCodec(false);
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
			final JsonCodec codec = new JsonCodec(obj, new ReadOnlyExternalFileHandler(assetPath.getParent(), assetPath), true, data.formatVersion());
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
						final JsonObject obj = new JsonObject();
						final JsonCodec codec = new JsonCodec(obj, new WriteOnlyExternalFileHandler(dest.getParent(), dest), false, data.formatVersion());
						asset.writeWithId(codec);
						Files.writeString(dest, FinmerProjectWriter.toJson(obj), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

	static final record ShowMetadata() implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to show metadata of");
			System.out.println("! Furball metadata:");
			System.out.printf("ID: %s\n", furball.meta.id);
			System.out.printf("Title: %s\n", furball.meta.title);
			System.out.printf("Author: %s\n", furball.meta.author);
			System.out.printf("Format Version: %d\n", furball.meta.formatVersion);
		}
	}

	static final record ListDependencies() implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to list dependencies of");
			System.out.printf("! %d dependenc%s:\n", furball.dependencies.size(), furball.dependencies.size() == 1 ? "y" : "ies");

			for (FurballDependency dep : furball.dependencies)
				System.out.printf("%s  %s\n", dep.id(), dep.filename());
		}
	}

	static final record ListAssets() implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to list assets of");
			System.out.printf("! %d asset%s:\n", furball.assets.size(), furball.assets.size() == 1 ? "" : "s");

			for (FurballAsset asset : furball.assets) {
				String str = asset.getClass().getSimpleName();
				str = str.substring(0, str.length() - "Asset".length());
				System.out.printf("%s  %s  %s\n", asset.id, asset.filename, str);
			}
		}
	}

	static final record ListShufflers() implements Step {
		@Override
		public void run(WorkingData data) {
			final var registry = AssetShufflerRegistry.getRegistry();

			System.out.printf("! There are %d registered shufflers:\n", registry.size());

			for (Map.Entry<String, AssetShuffler<?>> entry : registry.entrySet())
				System.out.printf("%s: %s\n", entry.getKey(), entry.getValue().description());

			System.out.printf("! To apply all of them: --shuffle %s\n", String.join(",", registry.keySet()));
		}
	}

	static final record Shuffle(String keys, @Nullable String seed) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final Furball furball = data.furball("no furball loaded to shuffle");

			final List<String> realKeys = List.of(keys.split(","));

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