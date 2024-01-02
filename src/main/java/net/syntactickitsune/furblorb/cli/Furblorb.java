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

	private static final String VERSION = "1.0.0";
	private static final String HELP = """
Furblorb: an unpacker/packer for Finmer "furballs"

Usage: java -jar Furblorb-%VERSION%.jar <arguments...>
Ex. 1: java -jar Furblorb-%VERSION%.jar --read Core.furball --write Core
Ex. 2: java -jar Furblorb-%VERSION%.jar --read Core --write Core.furball

Informational options:

  --help
          Print this text and exit.
  --version
          Print Furblorb's version and exit.

Note: "furball" in the following sections may be either a
      furball (.furball file) or a Finmer project (directory).

I/O options:

  --read <file>
          Read from the specified file.
          The file must be a valid furball.
  --write <file>
          Write to the specified file. Furblorb assumes
          it should write a furball if the filename ends
          in ".furball" and that it should write a
          Finmer project otherwise.
  --format-version <version>
          Write in the specified format version.
          Warning: changing the format version can
          and probably will cause problems!
          As Minecraft would say, "Here be dragons!"

Analysis options:

  --show-metadata
          Display the metadata of the read furball.
  --list-dependencies
          List all dependencies of the read furball.
  --list-assets
          List all assets of the read furball.

Manipulation options (these affect the read furball):

  --change-title <value>
          Set the title to the specified value.
  --change-author <value>
          Set the author to the specified value.
  --add-dependency <ID> <filename>
          Add a dependency on a furball or project with
          the specified ID and filename.
  --drop-dependency <ID>
          Remove any dependencies on the specified ID.

  --insert-asset <filename>
          Insert the specified Json asset.
  --extract-asset-id <ID> <destination>
          Extract the Json asset with the specified ID and
          write it to the specified file.
  --extract-asset-name <filename> <destination>
          Extract the Json asset with the specified filename
          and write it to the specified file.
  --drop-asset-id <ID>
          Remove ("drop") the asset with the specified ID.
  --drop-asset-name <filename>
          Remove ("drop") the asset with the specified filename.
  --merge <file>
          Merge the specified furball into the read furball. Any
          dependencies that the specified file has will be
          inherited by the read file, except for dependencies on
          the read file itself. All other metadata on the specified
          file will be discarded. Assets will replace any assets in
          the read furball that they share an ID with.
""";

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

				case "--help" -> {
					System.out.print(HELP.replace("%VERSION%", VERSION));
					System.exit(0);
				}
				case "--version" -> {
					System.out.println(VERSION);
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