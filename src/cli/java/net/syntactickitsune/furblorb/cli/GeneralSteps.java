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

import com.google.gson.GsonBuilder;

import net.syntactickitsune.furblorb.finmer.FinmerSaveData;
import net.syntactickitsune.furblorb.finmer.Furball;
import net.syntactickitsune.furblorb.finmer.FurballDependency;
import net.syntactickitsune.furblorb.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.finmer.asset.CreatureAsset;
import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.finmer.asset.ItemAsset;
import net.syntactickitsune.furblorb.finmer.asset.JournalAsset;
import net.syntactickitsune.furblorb.finmer.asset.SceneAsset;
import net.syntactickitsune.furblorb.finmer.asset.ScriptAsset;
import net.syntactickitsune.furblorb.finmer.asset.StringTableAsset;
import net.syntactickitsune.furblorb.finmer.component.PropertyContainer;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.finmer.io.FurballReader;
import net.syntactickitsune.furblorb.finmer.io.FurballWriter;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectReader.ReadOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.finmer.io.FinmerProjectWriter.WriteOnlyExternalFileHandler;
import net.syntactickitsune.furblorb.io.codec.BinaryCodec;
import net.syntactickitsune.furblorb.io.codec.CodecMode;
import net.syntactickitsune.furblorb.io.codec.JsonCodec;

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
			final boolean isFurball;

			try {
				if (filename.endsWith(".fnproj")) {
					data.setFurball(new FinmerProjectReader(ReadOnlyExternalFileHandler.forProjectFile(from)).readFurball());
					kind = "Finmer project";
					isFurball = true;
				} else if (filename.endsWith(".furball")) {
					data.setFurball(new FurballReader(Files.readAllBytes(from)).readFurball());
					kind = "furball";
					isFurball = true;
				} else if (filename.endsWith(".sav.json")) {
					final JsonCodec codec = new JsonCodec(FurblorbUtil.readJson(Files.readAllBytes(from)), null, CodecMode.READ_ONLY);
					data.setSave(new FinmerSaveData(codec));
					kind = "json save data";
					isFurball = false;
				} else if (filename.endsWith(".sav")) {
					data.setSave(new FinmerSaveData(new BinaryCodec(Files.readAllBytes(from), CodecMode.READ_ONLY)));
					kind = "binary save data";
					isFurball = false;
				} else
					throw new CliException("don't know how to read from " + filename + ", it does not seem to be a furball (.furball), a project (.fnproj), binary save data (.sav), or json save data (.sav.json)");
			} catch (NoSuchFileException e) {
				throw new CliException("file \"" + e.getFile() + "\" does not exist; cannot read it");
			} catch (AccessDeniedException e) {
				throw new CliException("could not read file \"" + e.getFile() + "\": access denied");
			}

			if (isFurball) {
				final Furball furball = data.furball;
				System.out.printf("! Read %s \"%s\" by %s with %d assets (format version %d).\n", kind,
						furball.meta.title, furball.meta.author, furball.assets.size(), furball.meta.formatVersion);

				check(furball);
			} else {
				final FinmerSaveData save = data.save;
				String desc = save.description;
				if (desc.startsWith("[M] ")) desc = desc.substring(4);

				final int newlineIndex = desc.indexOf("\r\n");
				System.out.printf("! Read %s of \"%s\" at \"%s\".\n", kind,
						desc.substring(0, newlineIndex),
						desc.substring(newlineIndex + 2));
			}
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
			final String filename = to.getFileName().toString();
			final boolean isSave = filename.endsWith(".sav") || filename.endsWith(".sav.json");
			if (!isSave && !filename.endsWith(".furball") && !filename.endsWith(".fnproj"))
				throw new CliException("don't know how to write to " + filename + ", it does not seem to be a furball (.furball), a project (.fnproj), binary save data (.sav), or json save data (.sav.json)");

			final String kind = isSave ? writeSave(data, filename) : writeFurball(data, filename);

			System.out.printf("! Completed: wrote %s to %s\n", kind, to().toAbsolutePath());
		}

		private String writeFurball(WorkingData data, String filename) throws Exception {
			final Furball furball = data.furball("no loaded furball to write");

			if (data.formatVersion != null && data.formatVersion != furball.meta.formatVersion) {
				if (data.formatVersion > furball.meta.formatVersion)
					System.out.println("! Upgrading format version to " + data.formatVersion + " (up from " + furball.meta.formatVersion + ").");
				else
					System.out.println("! Downgrading format version to " + data.formatVersion + " (down from " + furball.meta.formatVersion + ").");

				furball.meta.formatVersion = data.formatVersion;
			}

			final String kind;

			try {
				if (filename.endsWith(".furball")) {
					final BinaryCodec codec = new BinaryCodec(CodecMode.WRITE_ONLY);
					new FurballWriter(codec).write(furball);

					Files.write(to, codec.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

					kind = "a furball";
				} else if (filename.endsWith(".fnproj")) {
					new FinmerProjectWriter(WriteOnlyExternalFileHandler.forProjectFile(to)).writeFurball(furball);
					kind = "a Finmer project";
				} else
					throw new AssertionError("unreachable");
			} catch (AccessDeniedException e) {
				throw new CliException("could not write to file \"" + e.getFile() + "\": access denied");
			}

			return kind;
		}

		private String writeSave(WorkingData data, String filename) throws Exception {
			final FinmerSaveData save = data.save("no loaded save data to write");

			final String kind;

			try {
				if (filename.endsWith(".sav")) {
					final BinaryCodec codec = new BinaryCodec(CodecMode.WRITE_ONLY);
					save.write(codec);

					Files.write(to, codec.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

					kind = "binary save data";
				} else if (filename.endsWith(".sav.json")) {
					final JsonCodec codec = new JsonCodec(null, (byte) 0);
					save.write(codec);

					final String json = new GsonBuilder()
							.setPrettyPrinting()
							.disableHtmlEscaping()
							.create().toJson(codec.unwrap());

					Files.writeString(to, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

					kind = "json save data";
				} else
					throw new AssertionError("unreachable");
			} catch (AccessDeniedException e) {
				throw new CliException("could not write to file \"" + e.getFile() + "\": access denied");
			}

			return kind;
		}
	}

	static final record Show(boolean verbose) implements Step {
		@Override
		public void run(WorkingData data) throws Exception {
			if (data.furball != null)
				showFurball(data.furball);
			else if (data.save != null)
				showSave(data.save);
			else
				throw new CliException("no loaded furball or save data to show");
		}

		private void showFurball(Furball furball) {
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

		private void showSave(FinmerSaveData save) {
			System.out.println("\n! Save data metadata:");
			System.out.printf("Format Version:   %d\n", save.formatVersion);
			System.out.printf("Game Version:     %d.%d.%d\n", save.playerData.getInt("VERSION_MAJOR"),
					save.playerData.getInt("VERSION_MINOR"), save.playerData.getInt("VERSION_REV"));
			System.out.printf("Modded?:          %s\n", save.isModded() ? "yes" : "no");
			System.out.printf("Player:           %s, Lv %d %s\n",
					save.playerData.getString("name"),
					save.playerData.getInt("level"),
					FurblorbUtil.capitalize(save.playerData.getString("species")));
			System.out.printf("Time:             Day %d, Hour %d\n", save.playerData.getInt("TIME_DAY"), save.playerData.getInt("TIME_HOUR"));

			System.out.printf("\n! Loaded modules (%d):\n", save.modules.size());
			for (UUID id : save.modules)
				System.out.printf("- %s%s\n", id, FinmerSaveData.CORE_ID.equals(id) ? " (Finmer Core)" : "");

			System.out.println("\n! Player stats:");
			System.out.printf("Level:           %d (%d XP)\n", save.playerData.getInt("LEVEL"), save.playerData.getInt("XP"));
			System.out.printf("Points:          %d\n", save.playerData.getInt("ABILITY_POINTS"));
			System.out.printf("Strength:        %d\n", save.playerData.getInt("STR"));
			System.out.printf("Agility:         %d\n", save.playerData.getInt("AGI"));
			System.out.printf("Body:            %d\n", save.playerData.getInt("BODY"));
			System.out.printf("Wits:            %d\n", save.playerData.getInt("WITS"));
			System.out.printf("Prey Swallowed:  %d\n", save.playerData.getInt("NUM_PREY_SWALLOWED"));
			System.out.printf("Prey Digested:   %d\n", save.playerData.getInt("NUM_PREY_DIGESTED"));
			System.out.printf("Money:           %d coin%s\n", save.playerData.getInt("MONEY"), save.playerData.getInt("MONEY") == 1 ? "" : "s");

			if (verbose)
				showSaveProgress(save);
		}

		// This totally isn't a maintenance nightmare. I don't know what you're talking about.
		private void showSaveProgress(FinmerSaveData save) {
			final PropertyContainer ext = save.playerData.getPropertyContainer("EXTDATA");
			System.out.println("\n! Progress (Core v1.0.1):");

			// ==============================
			// MAIN QUEST:
			// 1) A River Apart
			// 2) Volatile When Mixed
			// 3) Servant of the People
			// 4) Partner in Crime
			// 5) Heaven and Earth
			// 6) Love Like Fire
			int progress = -1;
			if (ext.getBoolean("LUA_MQ01_STARTED")) progress = 0;
			if (ext.getBoolean("LUA_MQ01_DONE")) progress = 1;
			if (ext.getBoolean("LUA_MQ02_DONE")) progress = 2;
			if (ext.getBoolean("LUA_MQ03_DONE")) progress = 3;
			if (ext.getBoolean("LUA_MQ04_DONE")) progress = 4;
			// There is no MQ05_DONE flag, so here we just fudge it a little.
			if (ext.getBoolean("LUA_MQ05_MAW_ROOM_WORKSHOP_MAPS")) progress = 5;
			if (ext.getBoolean("LUA_MQ06_DONE")) progress = 6;
			if (progress >= 6)
				System.out.printf("Main Story:              Completed\n");
			else
				System.out.printf("Main Story:              %s (%d/6)\n", switch (progress) {
					case -1 -> "Not started";
					case 0 -> "A River Apart";
					case 1 -> "Volatile When Mixed";
					case 2 -> "Servant of the People";
					case 3 -> "Partner in Crime";
					case 4 -> "Heaven and Earth";
					case 5 -> "Love Like Fire";
					default -> "Error";
				}, progress);
			// ==================================
			// SIDE QUEST 1: Hazardous Occupation
			// 1) Started
			// 2) Finished
			progress = -1;
			final float sq1 = ext.getFloat("LUA_SQ01");
			if (sq1 == 1) progress = 0;
			if (sq1 == 2) progress = 1;
			System.out.printf("Hazardous Occupation:    %s\n", switch (progress) {
				case -1 -> "Not started";
				case 0 -> "In progress";
				case 1 -> "Completed";
				default -> "Error";
			});
			// ==================================
			// SIDE QUEST 2: Leave Only Pawprints
			// 1) Started
			// 2) Completed (slain)
			// 3) Completed (endo)
			// 999) Found Rux's cabin; quest locked
			System.out.printf("Leave Only Pawprints:    %s\n", switch ((int) ext.getFloat("LUA_SQ02")) {
				case 0 -> "Not started";
				case 1 -> "In progress";
				case 2 -> "Completed (slain)";
				case 3 -> "Completed (peaceful)";
				case 999 -> "Ineligible (locked by main story)";
				default -> "Error";
			});
			// ==================================
			// SIDE QUEST 3: A Hammer in Need
			progress = 0;
			if (ext.getBoolean("LUA_SQ03_STARTED")) progress = 1;
			System.out.printf("A Hammer in Need:        %s\n", switch (progress) {
				case 0 -> "Not started";
				case 1 -> "Probably completed"; // Hmmm. (We need to examine either shop data or journal data to determine completion. I'm not doing either of those right now.)
				default -> "Error";
			});
			// ==================================
			// SIDE QUEST 4: The Club
			progress = -1;
			final float sq4 = ext.getFloat("SQ04");
			if (sq4 == 1) progress = 0;
			if (ext.getBoolean("LUA_TOWN_PIT_UNLOCKED")) progress = 1;
			if (ext.getBoolean("LUA_TOWN_PIT_OPP1_WON")) progress = 2;
			if (ext.getBoolean("LUA_TOWN_PIT_OPP2_WON")) progress = 3;
			if (ext.getBoolean("LUA_TOWN_PIT_OPP3_WON")) progress = 4;
			if (ext.getBoolean("LUA_TOWN_PIT_OPP4_WON")) progress = 5;
			System.out.printf("The Club:                %s\n", switch (progress) {
				case -1 -> "Not started";
				case 0 -> "In progress";
				case 1 -> "Fought 0/4 opponents";
				case 2 -> "Fought 1/4 opponents";
				case 3 -> "Fought 2/4 opponents";
				case 4 -> "Fought 3/4 opponents";
				case 5 -> "Completed";
				default -> "Error";
			});
			// ==================================
			// SIDE QUEST 5: When We Were Both Cats
			progress = -1;
			if (ext.getBoolean("LUA_SQ05_DONE")) progress = 1;
			System.out.printf("When We Were Both Cats:  %s\n", switch (progress) {
				case -1 -> "Not started";
				case 1 -> "Completed";
				default -> "Error";
			});
			// ==================================
		}

		private static String count(Furball furball, Class<? extends FurballAsset> clazz) {
			final long count = furball.assets.stream().filter(clazz::isInstance).count();
			final int fullCount = furball.assets.size();
			return "%d (%.1f%%)".formatted(count, ((double) count / fullCount * 100));
		}
	}
}