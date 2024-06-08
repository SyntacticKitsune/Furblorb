package net.syntactickitsune.furblorb.cli;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import net.syntactickitsune.furblorb.finmer.io.FurballWriter;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ReadOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter.WriteOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;

/**
 * A container class for some of the {@link Step} implementations.
 * @author SyntacticKitsune
 */
final class GeneralSteps {

	static final record Read(Path from) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			final String kind;

			final String filename = from.getFileName().toString();

			try {
				if (filename.endsWith(".fnproj")) {
					data.setFurball(new FinmerProjectReader(ReadOnlyExternalFileHandler.forProjectFile(from)).readFurball());
					kind = "Finmer project";
				} else if (filename.endsWith(".furball")) {
					data.setFurball(new FurballReader(Files.readAllBytes(from)).readFurball());
					kind = "furball";
				} else
					throw new CliException("don't know how to read from " + filename + ", it does not seem to be a furball (.furball) or project (.fnproj)");
			} catch (NoSuchFileException e) {
				throw new CliException("file \"" + e.getFile() + "\" does not exist; cannot read it");
			} catch (AccessDeniedException e) {
				throw new CliException("could not read file \"" + e.getFile() + "\": access denied");
			}

			final Furball furball = data.furball;
			System.out.printf("! Read %s \"%s\" by %s with %d assets (format version %d).\n", kind,
					furball.meta.title, furball.meta.author, furball.assets.size(), furball.meta.formatVersion);

			check(furball);
		}

		private void check(Furball furball) {
			final Map<UUID, String> assetsById = new HashMap<>();

			for (FurballAsset asset : furball.assets) {
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
}