# Furblorb

Furblorb is a command-line tool for reading/writing furballs and [Finmer](https://get.finmer.dev) projects.
In particular, it lets you turn these projects into furballs *and* turn furballs back into projects.
One could (but probably shouldn't) think of it as being simultaneously a compiler and decompiler.

> [!WARNING]
> From [Finmer](https://github.com/pileofwolves/finmer)'s README (edited slightly): Finmer contains adult fiction and fantasy content. Proceed at your own risk. If you stumbled across this project by accident, chances are you're not interested in the subject matter and should move on. :)

## What *is* a furball?

Furballs are Finmer's compiled game data format.
One assumes that the name stems from "tarball" (another name for `.tar` archives).
The data format itself is basically your run-of-the-mill bespoke binary data -- that is, it's a binary format hyper-specialized for Finmer's game engine.
This isn't necessarily a bad thing; it means that the file format doesn't need to store extra information like field names or data types.
(On the other hand, this necessitates a format version and usually precludes random access.)

## What is a *furblorb*?

To start with, "blorb" is the name of a handful of very similar file formats for different kinds of interactive fiction formats.
The most common of which probably being Z-blorbs (for [Z-Machine](https://en.wikipedia.org/wiki/Z-machine) games) and G-blorbs (for Glulx games).
There's more history to it than that, but that's the basics.

Furblorb then is a combination of "fur" and "blorb".
The similarities in concept between furballs and blorbs is what lead me to choose the name "Furblorb", since furballs tick all the boxes:
* It's a binary data format
* It contains a game, or game data
* It's used for interactive fiction

## Usage

For full usage information, just run the jar (or pass the `--help` option), like so:

```
java -jar Furblorb-<version>-cli.jar
```

Furblorb can be used to pack a project into a furball:

```
java -jar Furblorb-<version>-cli.jar --read <path to project file>.fnproj --sort --write <file>.furball
```

```
$ java -jar Furblorb-<version>-cli.jar --read DeepForest/DeepForest.fnproj --sort --write DeepForest.furball
! The furblorb spell: safely protect a small creature as though in a strong box.
! Read Finmer project "Deep Forest" by SyntacticKitsune with 11 assets (format version 20).
! Sorted all assets.
! Completed: wrote a furball to DeepForest.furball
```

Although not required, it's encouraged to pass `--sort` like in the above example so that the created furball is reproducible on different filesystems.
(Since otherwise the order the assets are read in is dependant on the order the filesystem has them in, which could differ between computers.)

To unpack a furball into a project:

```
java -jar Furblorb-<version>-cli.jar --read <file>.furball --write <path to project file>.fnproj
```

```
$ java -jar Furblorb-<version>-cli.jar --read Core.furball --write Core/Core.fnproj
! The furblorb spell: safely protect a small creature as though in a strong box.
! Read furball "Finmer Core" by Nuntis the Wolf with 175 assets (format version 20).
! Completed: wrote a Finmer project to Core/Core.fnproj
```

Two furballs can be merged to create a single one:

```
java -jar Furblorb-<version>-cli.jar --read <file>.furball --merge <other file>.furball --write <output file>.furball
```

```
$ java -jar Furblorb-<version>-cli.jar --read Core.furball --merge DeepForest.furball --write CoreMerged.furball
! The furblorb spell: safely protect a small creature as though in a strong box.
! Read furball "Finmer Core" by Nuntis the Wolf with 175 assets (format version 20).
! Attempting to merge DeepForest.furball...
! Read furball "Deep Forest" by SyntacticKitsune with 11 assets (format version 20).
! Merged 0 dependencies and 11 assets from Deep Forest (e0ba8127-53dd-4c97-8f7f-0b079edeaae5) into Finmer Core (edcf99d2-6ced-40fa-87e9-86cda5e570ee).
! Completed: wrote a furball to CoreMerged.furball
```

The last major thing: a furball can be "shuffled", in that all of its text/items/etc. are shuffled so they appear in different places (like in Undertale corruptions).
This can be done like so:

```
java -jar Furblorb-<version>-cli.jar --read <file>.furball --shuffle <comma-separated list of shufflers to apply, or "all"> <seed for the randomizer> --write <output file>.furball
```

```
$ java -jar Furblorb-<version>-cli.jar --read Core.furball --shuffle all 12345 --write "CoreShuffled.furball"
! The furblorb spell: safely protect a small creature as though in a strong box.
! Read furball "Finmer Core" by Nuntis the Wolf with 175 assets (format version 20).
! Shuffling 63 assets using shuffler itemflavortext.
! Shuffling 63 assets using shuffler itemnames.
! Shuffling 32 assets using shuffler scenechoicetext.
! Shuffling 24 assets using shuffler strings.
! Shuffling completed.
! Completed: wrote a furball to CoreShuffled.furball
```

Internally, Furblorb translates each argument as a separate step to apply (mostly), so multiple read and write operations can be chained like so:

```
java -jar Furblorb-<version>-cli.jar --read <file 1>.furball --write <file 1 output>.fnproj --read <file 2>.furball --write <file 2 output>.fnproj
```

This also means that the order the arguments appear in matters -- trying to do something requiring a furball before it's loaded will cause errors.

## Licensing

Furblorb itself is licensed under the GNU Lesser General Public License version 3 ([GNU LGPLv3](LICENSE.LESSER.md)).

However, it also includes a few copies of "Finmer Core" by Nuntis and a copy of "Deep Forest" by SyntacticKitsune in its testing code (see `src/test/resources`).
This data is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International ([CC BY-NC-SA](https://creativecommons.org/licenses/by-nc-sa/4.0/)) license.
These copies are only used for verifying the correctness of the program, and are not included in the compiled binaries.

## Compiling

Furblorb can be compiled using one of the following commands:

On Linux/Mac: `./gradlew build`<br>
On Windows: `gradlew.bat build`

The built binaries can then be found in `build/libs`:

* `Furblorb-<version>-io.jar` contains only the IO classes -- that is, no Finmer-related stuff.
* `Furblorb-<version>-io-sources.jar` contains sources for the IO jar.
* `Furblorb-<version>-finmer.jar` contains only the Finmer-related classes, it requires the IO jar.
* `Furblorb-<version>-finmer-sources.jar` contains sources for the Finmer jar.
* `Furblorb-<version>-cli.jar` contains both the IO and Finmer things, along with the CLI.
* `Furblorb-<version>-sources.jar` contains sources for all of the above.

## Java API

Furblorb also has an almost-fully-documented Java API which can be used to manipulate furballs instead of using the CLI.
See `FurballReader`, `FurballWriter`, `FinmerProjectReader`, and `FinmerProjectWriter` for reading/writing furballs and projects, respectively.

The `io` package and subpackages has some neat utilities. `BinaryCodec` for instance, which can be used to read and write data compatible with C#'s [`BinaryReader`](https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader) and [`BinaryWriter`](https://learn.microsoft.com/en-us/dotnet/api/system.io.binarywriter) classes. In particular it supports [7-bit ints](https://learn.microsoft.com/en-us/dotnet/api/system.io.binaryreader.read7bitencodedint) as well as `GUID`'s bespoke format. In fact, `Codec` allows reading/writing data in a format-agnostic way, whether structured or unstructured -- the builtin implementations support binary and JSON, but new ones could support data formats like XML, YAML, etc.

## Furball format versions

Furblorb can read/write in a handful of format versions.
Here's some information on them (which is mostly a sort-of changelog).

#### v21 (in progress)

<u>This format version is not in any released build of Finmer yet.</u>

Most notably, this format version decreases the file size of furballs by doing the following:
* using 7-bit integers (or "var ints") instead of normal integers for lists, byte arrays, and various asset properties
* GZIP-compressing everything after the format version
* eliding the presence flag for required serializables in e.g. lists

It also introduces an API for checking the presence of modules and assets, and backports the Lua 5.2 `bit32` library.
These are not (yet?) exposed to visual scripting, however.

Furblorb currently has partial support for this format version, although not in any released version of the program, and not enabled by default.

#### v20 (2023-12-18 ~ present)

This format version came out with Finmer v1.0.1.

Most notably, this format version adds support for "game starts."
These allow selecting a *different* game start, so one can have the Core module installed but be playing a completely different story.
This necessitated two new fields in scene assets:
* `IsGameStart`
* `GameStartDescription`

It also introduced some new visual scripting bits:
* `OnRoundStart` in the `CombatBegin` command
* The `TimeAdvance` command
* The `TimeSetHour` command
* The `TimeDay` condition
* The `TimeHour` condition
* The `TimeHourTotal` condition
* The `TimeIsNight` condition

Number setting in visual scripting got some new value sources:
* `Random`
* `TimeDay`
* `TimeHour`
* `TimeHourTotal`
* `PlayerStrength`
* `PlayerAgility`
* `PlayerBody`
* `PlayerWits`
* `PlayerMoney`
* `PlayerLevel`
* `PlayerHealth`
* `PlayerHealthMax`

#### v19 (2023-02-26 ~ 2023-12-18)

This is the first format version Furblorb supports, and also the version that comes with Finmer v1.0.0.