package net.syntactickitsune.furblorb.cli;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.Furball;

final class WorkingData {

	Furball furball;
	@Nullable
	Byte formatVersion;

	byte formatVersion() {
		return formatVersion != null ? formatVersion : furball.meta.formatVersion;
	}
}