package net.syntactickitsune.furblorb.cli;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;

/**
 * The main entrypoint and CLI of Furblorb.
 * @author SyntacticKitsune
 */
public final class Furblorb {

	private static final String VERSION;
	private static final String HELP;

	static {
		final String version = String.join("\n", FurblorbUtil.readStringResource("/version.txt"));
		VERSION = "${version}".equals(version) ? "dev" : version;

		HELP = String.join("\n", FurblorbUtil.readStringResource("/usage.txt"));
	}

	public static void main(String[] args) throws IOException {
		final Config cfg = readArgs(args);

		if (cfg.steps.isEmpty()) {
			System.out.println("Missing any steps to perform.");
			System.exit(1);
		}

		FurballSerializables.lookupById(0); // Class-load FurballSerializables.

		System.out.println("! The furblorb spell: safely protect a small creature as though in a strong box.");

		final WorkingData data = new WorkingData();

		for (Step step : cfg.steps)
			try {
				step.run(data);
			} catch (CliException e) {
				System.err.println(e.logMessage());
				System.exit(1);
			} catch (Exception e) {
				System.err.println("! Error: an exception occurred.");
				e.printStackTrace();
				System.exit(1);
			}
	}

	private static Config readArgs(String[] args) {
		if (args.length == 0) {
			System.out.print(HELP.replace("%VERSION%", VERSION));
			System.exit(0);
		}

		int skip = 0;
		final List<Step> steps = new ArrayList<>();

		for (int i = 0; i < args.length; i++) {
			if (skip > 0) {
				skip--;
				continue;
			}

			switch (args[i]) {
				case "--read" -> {
					if (i + 1 == args.length)
						System.out.println("--read: expected a file argument.");
					else {
						steps.add(new GeneralSteps.Read(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--write" -> {
					if (i + 1 == args.length)
						System.out.println("--write: expected a file argument.");
					else {
						steps.add(new GeneralSteps.Write(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--format-version" -> {
					if (i + 1 == args.length)
						System.out.println("--format-version: expected a version argument.");
					else {
						steps.add(new FurballSteps.AdjustFormatVersion(Byte.parseByte(args[i + 1])));
						skip = 1;
					}
				}

				case "--show" -> {
					steps.add(new GeneralSteps.Show(false));
				}

				case "--show-full" -> {
					steps.add(new GeneralSteps.Show(true));
				}

				case "--change-title" -> {
					if (i + 1 == args.length)
						System.out.println("--change-title: expected a new title argument.");
					else {
						steps.add(new FurballSteps.ChangeTitle(args[i + 1]));
						skip = 1;
					}
				}
				case "--change-author" -> {
					if (i + 1 == args.length)
						System.out.println("--change-author: expected a new author argument.");
					else {
						steps.add(new FurballSteps.ChangeAuthor(args[i + 1]));
						skip = 1;
					}
				}
				case "--add-dependency" -> {
					if (i + 2 >= args.length)
						System.out.println("--add-dependency: expected two arguments.");
					else {
						steps.add(new FurballSteps.AddDependency(UUID.fromString(args[i + 1]), args[i + 2]));
						skip = 2;
					}
				}
				case "--drop-dependency" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-dependency: expected an ID argument.");
					else {
						steps.add(new FurballSteps.DropDependency(UUID.fromString(args[i + 1])));
						skip = 1;
					}
				}
				case "--insert-asset" -> {
					if (i + 1 == args.length)
						System.out.println("--insert-asset: expected a file argument.");
					else {
						steps.add(new FurballSteps.InsertAsset(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--extract-asset-id" -> {
					if (i + 2 >= args.length)
						System.out.println("--extract-asset-id: expected two arguments.");
					else {
						steps.add(new FurballSteps.ExtractAsset(null, UUID.fromString(args[i + 1]), Paths.get(args[i + 2])));
						skip = 2;
					}
				}
				case "--extract-asset-name" -> {
					if (i + 2 >= args.length)
						System.out.println("--extract-asset-name: expected two arguments.");
					else {
						steps.add(new FurballSteps.ExtractAsset(args[i + 1], null, Paths.get(args[i + 2])));
						skip = 2;
					}
				}
				case "--drop-asset-id" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-asset-id: expected an ID argument.");
					else {
						steps.add(new FurballSteps.DropAsset(null, UUID.fromString(args[i + 1])));
						skip = 1;
					}
				}
				case "--drop-asset-name" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-asset-name: expected a filename argument.");
					else {
						steps.add(new FurballSteps.DropAsset(args[i + 1], null));
						skip = 1;
					}
				}
				case "--merge" -> {
					if (i + 1 == args.length)
						System.out.println("--merge-asset: expected a file argument.");
					else {
						steps.add(new FurballSteps.Merge(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--sort-assets" -> {
					steps.add(new FurballSteps.SortAssets());
				}
				case "--shuffle" -> {
					if (i + 2 == args.length)
						System.out.println("--shuffle: expected two arguments.");
					else {
						steps.add(new FurballSteps.Shuffle(args[i + 1], args[i + 2]));
						skip = 2;
					}
				}

				case "--help" -> {
					System.out.print(HELP.replace("%VERSION%", VERSION));
					System.exit(0);
				}
				case "--version" -> {
					System.out.println(VERSION);
					System.exit(0);
				}
				case "--list-shufflers" -> {
					new FurballSteps.ListShufflers().run(null);
					System.exit(0);
				}

				default -> {
					System.out.println(args[i] + ": unrecognized argument.");
				}
			}
		}

		return new Config(steps);
	}

	private static record Config(List<Step> steps) {}
}