package net.syntactickitsune.furblorb.finmer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.component.PropertyContainer;
import net.syntactickitsune.furblorb.finmer.io.UnsupportedFormatVersionException;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.SequenceDecoder;
import net.syntactickitsune.furblorb.io.SequenceEncoder;

/**
 * <p>
 * {@code FinmerSaveData} is Furblorb's version of Finmer's save data.
 * In particular, it's a kind of amalgamation of {@code GameSnapshot}, {@code SaveData}, and parts of {@code SaveManager}.
 * </p>
 * <p>
 * {@code FinmerSaveData} can be used to read Finmer's save files (the files named e.g. {@code Slot1.sav}).
 * {@link #FinmerSaveData(Decoder)} can be used to read one, and {@link #write(Encoder)} to write one.
 * </p>
 * @since 2.0.0
 */
public final class FinmerSaveData {

	/**
	 * The {@code UUID} of Finmer's Core module, which <i>must</i> be present at all times.
	 */
	public static final UUID CORE_ID = UUID.fromString("edcf99d2-6ced-40fa-87e9-86cda5e570ee");

	/**
	 * Represents the latest save file format version -- the only format version Furblorb can read.
	 */
	public static final byte LATEST_FORMAT_VERSION = 5;

	/**
	 * The format version of the save data.
	 * {@link #LATEST_FORMAT_VERSION} is the default.
	 */
	public byte formatVersion = LATEST_FORMAT_VERSION;

	/**
	 * The description of the save data.
	 * This is the text shown in the save selection panel that describes the character and location.
	 */
	public String description = "";

	/**
	 * A list of modules that were loaded when the save was created.
	 * Saves with modules missing from the current environment cannot be loaded.
	 */
	public final Set<UUID> modules = new LinkedHashSet<>();

	/**
	 * The {@link PropertyContainer} containing the player data, which easily makes up the bulk of all save data.
	 * This contains both the player's data, but also Lua-defined variables (prefixed with "LUA_") and shop data (prefixed with "SHOP_").
	 * It also contains the version of Finmer used to create the save file.
	 */
	public final PropertyContainer playerData;

	/**
	 * The {@link PropertyContainer} containing the data of the active scene.
	 * This is usually just a reference to the active scene itself and where in the scene the player is.
	 */
	public final PropertyContainer sceneData;

	/**
	 * The {@link PropertyContainer} containing the state of the interface.
	 * This most prominently contains the last however many lines of the log, but also contains the location and instruction information.
	 */
	public final PropertyContainer interfaceData;

	/**
	 * Constructs a new, empty {@code FinmerSaveData}.
	 */
	public FinmerSaveData() {
		playerData = new PropertyContainer();
		sceneData = new PropertyContainer();
		interfaceData = new PropertyContainer();
	}

	/**
	 * Decodes the {@code FinmerSaveData} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws UnsupportedFormatVersionException If the format version of the save data is not supported by Furblorb.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public FinmerSaveData(Decoder in) {
		formatVersion = in.readByte("FormatVersion");
		if (formatVersion != LATEST_FORMAT_VERSION)
			throw new UnsupportedFormatVersionException(formatVersion, "Unsupported save data format version " + formatVersion);

		description = in.readString("Description");
		modules.addAll(in.readListOf("LoadedModules", SequenceDecoder::readUUID));

		playerData = in.readObject("PlayerData", PropertyContainer::new);
		sceneData = in.readObject("SceneData", PropertyContainer::new);
		interfaceData = in.readObject("InterfaceData", PropertyContainer::new);
	}

	/**
	 * Determines whether the save data represents a modded game.
	 * A game is considered "modded" if it contains any modules other than Finmer's Core module.
	 * If the save data is considered modded, then an "M" shows up in the save data's description.
	 * @return {@code true} if the save data represents a modded game.
	 */
	// It's only modded if it comes from the modded region of
	// the Reaches, otherwise it's just sparkling save data.
	public boolean isModded() {
		return modules.size() > 1 || !modules.contains(CORE_ID);
	}

	/**
	 * Creates and returns the description of the save data, like if Finmer were to do it.
	 * @return The generated description.
	 */
	public String makeDescription() {
		return "%s%s  -  Lv %d %s\r\n%s".formatted(
				isModded() ? "[M] " : "", // Rated M for Mysterious
				playerData.getString("name"),
				playerData.getInt("level"),
				FurblorbUtil.capitalize(playerData.getString("species")),
				interfaceData.getString("ui_location")
				);
	}

	/**
	 * Writes this {@code FinmerSaveData} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeByte("FormatVersion", formatVersion);

		to.writeString("Description", description);

		to.writeListOf("LoadedModules", modules, SequenceEncoder::writeUUID);

		to.writeObject("PlayerData", playerData, PropertyContainer::write);
		to.writeObject("SceneData", sceneData, PropertyContainer::write);
		to.writeObject("InterfaceData", interfaceData, PropertyContainer::write);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof FinmerSaveData fsd)) return false;
		return formatVersion == fsd.formatVersion && Objects.equals(description, fsd.description)
				&& modules.equals(fsd.modules) && playerData.equals(fsd.playerData)
				&& sceneData.equals(fsd.sceneData) && interfaceData.equals(fsd.interfaceData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(formatVersion, description, modules, playerData, sceneData, interfaceData);
	}

	@Override
	public String toString() {
		return description;
	}
}