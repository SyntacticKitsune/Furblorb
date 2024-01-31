package net.syntactickitsune.furblorb.api.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.syntactickitsune.furblorb.FurblorbUtil;
import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballDependency;
import net.syntactickitsune.furblorb.api.FurballMetadata;
import net.syntactickitsune.furblorb.api.FurblorbException;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.io.impl.JsonCodec;
import net.syntactickitsune.furblorb.io.FurballSerializables;

/**
 * <p>
 * The {@code FinmerProjectReader} {@code class} creates {@link Furball Furballs} from Finmer projects.
 * That is, it reads every asset, script, and image from a provided directory and shoves them all into a packed {@code Furball}.
 * </p>
 * <p>
 * Using this class is as simple as:
 * <code><pre>
 * Furball furball = new FinmerProjectReader(projectDirectory).readFurball();</pre></code>
 * </p>
 * @author SyntacticKitsune
 * @see FinmerProjectWriter
 */
public final class FinmerProjectReader {

	private final ExtendedExternalFileHandler externalFiles;

	/**
	 * Constructs a new {@code FinmerProjectReader} with the specified external file handler.
	 * The file handler will be queried for all of the required files.
	 * @param externalFiles The external file handler.
	 * @throws NullPointerException If {@code externalFiles} is {@code null}.
	 */
	public FinmerProjectReader(ExtendedExternalFileHandler externalFiles) {
		this.externalFiles = Objects.requireNonNull(externalFiles, "externalFiles");
	}

	/**
	 * Constructs a new {@code FinmerProjectReader} with the specified Finmer project directory.
	 * @param projectDirectory The project directory to read from.
	 * @throws NullPointerException If {@code projectDirectory} is {@code null}.
	 */
	public FinmerProjectReader(Path projectDirectory) {
		this(new DefaultExternalFileHandler(projectDirectory));
	}

	private JsonObject readJson(String filename) {
		final byte[] bytes = externalFiles.readExternalFile(filename);
		if (bytes == null) throw new FurblorbParsingException("ExternalFileHandler returned null for " + filename);

		String j = new String(bytes, StandardCharsets.UTF_8);
		if (j.charAt(0) == 65279) j = j.substring(1); // Remove the BOM, if present.

		final JsonElement elem = JsonParser.parseString(j);
		return elem.getAsJsonObject();
	}

	private FurballMetadata readMetadata(JsonCodec codec) {
		FurballReader.checkFormatVersion(codec.formatVersion());
		return new FurballMetadata(codec, codec.formatVersion());
	}

	/**
	 * Attempts to read a {@link Furball} from the {@code FinmerProjectReader}'s {@link ExtendedExternalFileHandler}.
	 * @return The read furball.
	 * @throws FurblorbParsingException If a parsing error occurs.
	 * @throws UnsupportedFormatVersionException If the furball described by the data has a format version that cannot be read by this {@code FinmerProjectReader}.
	 */
	public Furball readFurball() {
		final List<String> files = externalFiles.listFiles();

		final JsonObject proj = readJson(externalFiles.projectFilename());
		final JsonCodec projCodec = new JsonCodec(proj, externalFiles, true, proj.get("FormatVersion").getAsByte());
		final FurballMetadata meta = readMetadata(projCodec);
		final Furball furball = new Furball(meta);

		{
			furball.dependencies.addAll(projCodec.readList("Dependencies", FurballDependency::new));
		}

		final List<String> assets = files.stream()
				.filter(file -> file.endsWith(".json"))
				.sorted()
				.toList();

		for (String asset : assets)
			try {
				final JsonObject obj = readJson(asset);
				final JsonCodec codec = new JsonCodec(obj, externalFiles, true, meta.formatVersion);
				furball.assets.add(FurballSerializables.read(codec));
			} catch (Exception e) {
				throw new FurblorbException("Exception reading asset " + asset, e);
			}

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

		return furball;
	}

	/**
	 * A slightly-extended version of {@link ExternalFileHandler} with support for listing files of the root directory.
	 * It also provides the name of the Finmer project.
	 * These extensions are required for discovery of project data and assets.
	 * @author SyntacticKitsune
	 */
	public static interface ExtendedExternalFileHandler extends ExternalFileHandler {

		/**
		 * @return The name of the project file. This is frequently the directory name + ".fnproj".
		 */
		public String projectFilename();

		/**
		 * Finds and returns a {@code List} consisting of all of the files found in the root directory.
		 * This method is used by {@link FinmerProjectReader} in order to discover the assets in a Finmer project.
		 * @return The files in the root directory.
		 */
		public List<String> listFiles();
	}

	/**
	 * The default reading implementation of {@link ExtendedExternalFileHandler}, which reads projects from a directory on the filesystem.
	 * @param projectDirectory The project directory to read from.
	 * @author SyntacticKitsune
	 */
	public static record DefaultExternalFileHandler(Path projectDirectory) implements ExtendedExternalFileHandler {

		/**
		 * Constructs a new {@code DefaultExternalFileHandler}.
		 * @param projectDirectory The project directory to read from.
		 * @throws NullPointerException If {@code projectDirectory} is {@code null}.
		 */
		public DefaultExternalFileHandler {
			Objects.requireNonNull(projectDirectory, "projectDirectory");
		}

		@Override
		public String projectFilename() {
			return projectDirectory.getFileName().toString() + ".fnproj";
		}

		@Override
		public List<String> listFiles() {
			try {
				return Files.list(projectDirectory)
						.map(path -> path.getFileName().toString())
						.toList();
			} catch (IOException e) {
				return FurblorbUtil.throwAsUnchecked(e);
			}
		}

		@Override
		@Nullable
		public byte[] readExternalFile(String filename) {
			final Path file = projectDirectory.resolve(filename);
			try {
				// We need to "normalize" the line endings of text files so that the furball is the expected size.
				if (filename.endsWith(".lua")) {
					String str = Files.readString(file);
					if (str.indexOf('\r') == -1)
						str = str.replace("\n", "\r\n");

					return str.getBytes(StandardCharsets.UTF_8);
				}

				return Files.readAllBytes(file);
			} catch (NoSuchFileException e) { // Atomic filesystems go brrr.
				return null;
			} catch (IOException e) {
				return FurblorbUtil.throwAsUnchecked(e);
			}
		}
	}
}