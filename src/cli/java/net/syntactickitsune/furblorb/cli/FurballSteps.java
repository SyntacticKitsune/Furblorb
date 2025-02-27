package net.syntactickitsune.furblorb.cli;

import java.nio.file.Files;
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

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.syntactickitsune.furblorb.cli.shuffling.AssetShuffler;
import net.syntactickitsune.furblorb.cli.shuffling.AssetShufflerRegistry;
import net.syntactickitsune.furblorb.cli.shuffling.ShuffleRandom;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.FurballDependency;
import net.syntactickitsune.furblorb.finmer.FurballUtil;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ReadOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter.WriteOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.codec.CodecMode;
import net.syntactickitsune.furblorb.io.codec.JsonCodec;

/**
 * A container class for all of the furball-related {@link Step} implementations.
 * @author SyntacticKitsune
 */
final class FurballSteps {

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
			System.out.printf("! Changed author %s => %s.", furball.meta.author, value);
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
			FurballUtil.initializeJsonCodec(codec);
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
						FurballUtil.initializeJsonCodec(codec);
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
			new GeneralSteps.Read(other).run(newData);
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