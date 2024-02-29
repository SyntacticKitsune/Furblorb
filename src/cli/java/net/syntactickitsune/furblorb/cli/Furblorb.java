package net.syntactickitsune.furblorb.cli;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.syntactickitsune.furblorb.FurblorbUtil;
import net.syntactickitsune.furblorb.io.FurballSerializables;

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
			} catch (Exception e) {
				FurblorbUtil.throwAsUnchecked(e);
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
						steps.add(new Steps.Read(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--write" -> {
					if (i + 1 == args.length)
						System.out.println("--write: expected a file argument.");
					else {
						steps.add(new Steps.Write(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--format-version" -> {
					if (i + 1 == args.length)
						System.out.println("--format-version: expected a version argument.");
					else {
						steps.add(new Steps.AdjustFormatVersion(Byte.valueOf(args[i + 1])));
						skip = 1;
					}
				}

				case "--show-metadata" -> {
					steps.add(new Steps.ShowMetadata());
				}
				case "--list-dependencies" -> {
					steps.add(new Steps.ListDependencies());
				}
				case "--list-assets" -> {
					steps.add(new Steps.ListAssets());
				}

				case "--change-title" -> {
					if (i + 1 == args.length)
						System.out.println("--change-title: expected a new title argument.");
					else {
						steps.add(new Steps.ChangeTitle(args[i + 1]));
						skip = 1;
					}
				}
				case "--change-author" -> {
					if (i + 1 == args.length)
						System.out.println("--change-author: expected a new author argument.");
					else {
						steps.add(new Steps.ChangeAuthor(args[i + 1]));
						skip = 1;
					}
				}
				case "--add-dependency" -> {
					if (i + 2 >= args.length)
						System.out.println("--add-dependency: expected two arguments.");
					else {
						steps.add(new Steps.AddDependency(UUID.fromString(args[i + 1]), args[i + 2]));
						skip = 2;
					}
				}
				case "--drop-dependency" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-dependency: expected an ID argument.");
					else {
						steps.add(new Steps.DropDependency(UUID.fromString(args[i + 1])));
						skip = 1;
					}
				}
				case "--insert-asset" -> {
					if (i + 1 == args.length)
						System.out.println("--insert-asset: expected a file argument.");
					else {
						steps.add(new Steps.InsertAsset(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--extract-asset-id" -> {
					if (i + 2 >= args.length)
						System.out.println("--extract-asset-id: expected two arguments.");
					else {
						steps.add(new Steps.ExtractAsset(null, UUID.fromString(args[i + 1]), Paths.get(args[i + 2])));
						skip = 2;
					}
				}
				case "--extract-asset-name" -> {
					if (i + 2 >= args.length)
						System.out.println("--extract-asset-name: expected two arguments.");
					else {
						steps.add(new Steps.ExtractAsset(args[i + 1], null, Paths.get(args[i + 2])));
						skip = 2;
					}
				}
				case "--drop-asset-id" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-asset-id: expected an ID argument.");
					else {
						steps.add(new Steps.DropAsset(null, UUID.fromString(args[i + 1])));
						skip = 1;
					}
				}
				case "--drop-asset-name" -> {
					if (i + 1 == args.length)
						System.out.println("--drop-asset-name: expected a filename argument.");
					else {
						steps.add(new Steps.DropAsset(args[i + 1], null));
						skip = 1;
					}
				}
				case "--merge" -> {
					if (i + 1 == args.length)
						System.out.println("--merge-asset: expected a file argument.");
					else {
						steps.add(new Steps.Merge(Paths.get(args[i + 1])));
						skip = 1;
					}
				}
				case "--sort-assets" -> {
					steps.add(new Steps.SortAssets());
				}
				case "--shuffle" -> {
					if (i + 2 == args.length)
						System.out.println("--shuffle: expected two arguments.");
					else {
						steps.add(new Steps.Shuffle(args[i + 1], args[i + 2]));
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
					new Steps.ListShufflers().run(null);
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