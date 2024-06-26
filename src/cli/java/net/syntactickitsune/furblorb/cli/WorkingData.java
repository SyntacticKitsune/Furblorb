package net.syntactickitsune.furblorb.cli;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.FinmerSaveData;
import net.syntactickitsune.furblorb.finmer.Furball;

/**
 * Context for a step, allowing certain data to be shared and mutated between steps.
 * @author SyntacticKitsune
 */
final class WorkingData {

	/**
	 * The current furball. Will be {@code null} if no furball has been read yet.
	 */
	@Nullable
	Furball furball;

	/**
	 * The current save data. Will be {@code null} if no save data has been read yet.
	 * @since 2.0.0
	 */
	@Nullable
	FinmerSaveData save;

	/**
	 * The current explicit format version. Will be {@code null} if no specific format version has been set.
	 * If a specific format version has been set, the furball will be upgraded/downgraded to it when written.
	 */
	@Nullable
	Byte formatVersion;

	/**
	 * Returns the target format version, whether that be {@linkplain #formatVersion a specific user-set one} or the {@linkplain #furball}'s current format version.
	 * @return The target format version.
	 */
	byte formatVersion() {
		return formatVersion != null ? formatVersion : furball.meta.formatVersion;
	}

	/**
	 * If the {@linkplain #furball furball} is non-{@code null}, returns it.
	 * Otherwise, throws a {@link CliException} with the specified message.
	 * @param nullMessage The message of the thrown {@code CliException}.
	 * @return The furball.
	 */
	Furball furball(String nullMessage) {
		if (furball == null) throw new CliException(nullMessage);
		return furball;
	}

	/**
	 * If the {@linkplain #save save data} is non-{@code null}, returns it.
	 * Otherwise, throws a {@link CliException} with the specified message.
	 * @param nullMessage The message of the thrown {@code CliException}.
	 * @return The save data.
	 * @since 2.0.0
	 */
	FinmerSaveData save(String nullMessage) {
		if (save == null) throw new CliException(nullMessage);
		return save;
	}

	void setFurball(Furball value) {
		save = null;
		furball = value;
	}

	void setSave(FinmerSaveData value) {
		furball = null;
		save = value;
	}
}