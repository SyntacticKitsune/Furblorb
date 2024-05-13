# Furblorb

Furblorb is a command-line tool for reading/writing furballs and [Finmer](https://get.finmer.dev) projects.
In particular, it lets you turn these projects into furballs *and* turn furballs back into projects.
One could think of it as being simultaneously a compiler and decompiler.

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

## Licensing

Furblorb itself is licensed under [GNU LGPLv3](LICENSE.LESSER.md), however, it also includes a few copies of "Finmer Core" by Nuntis and a copy of "Deep Forest" by SyntacticKitsune in its testing code (see `src/test/resources`).
This data is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International ([CC BY-NC-SA](https://creativecommons.org/licenses/by-nc-sa/4.0/)) license.
These copies are only used for verifying the correctness of the program, and are not included in the compiled binaries.

## Furball format versions

Furblorb can read/write in a handful of format versions.
Here's some information on them (which is mostly a sort-of changelog).

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