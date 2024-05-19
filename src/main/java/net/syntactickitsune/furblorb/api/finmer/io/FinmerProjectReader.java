package net.syntactickitsune.furblorb.api.finmer.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.syntactickitsune.furblorb.api.finmer.Furball;
import net.syntactickitsune.furblorb.api.finmer.FurballDependency;
import net.syntactickitsune.furblorb.api.finmer.FurballMetadata;
import net.syntactickitsune.furblorb.api.finmer.FurblorbUtil;
import net.syntactickitsune.furblorb.api.io.ExternalFileHandler;
import net.syntactickitsune.furblorb.api.io.FurblorbException;
import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.api.io.codec.JsonCodec;

/**
 * <p>
 * The {@code FinmerProjectReader} {@code class} creates {@link Furball Furballs} from Finmer projects.
 * That is, it reads every asset, script, and image from a provided directory and shoves them all into a packed {@code Furball}.
 * </p>
 * <p>
 * Using this class is as simple as:
 * <code><pre>
 * Furball furball = new FinmerProjectReader(ReadOnlyExternalFileHandler.fromProjectFile(projectFile)).readFurball();</pre></code>
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

	private JsonObject readJson(String filename) {
		final byte[] bytes = externalFiles.readExternalFile(filename);
		if (bytes == null) throw new FurblorbParsingException("ExternalFileHandler returned null for " + filename);
		return readJson(bytes);
	}

	private static JsonObject readJson(byte[] bytes) {
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

		final JsonObject proj = readJson(externalFiles.readProjectFile());
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
		 * Reads the contents of the project file into a new byte array and returns it.
		 * @return The project file bytes.
		 * @throws UnsupportedOperationException If the {@code ExternalFileHandler} does not support reading files (like if it's write-only).
		 */
		public default byte[] readProjectFile() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Writes the given bytes to the project file.
		 * @param contents The bytes to write to the project file.
		 * @throws NullPointerException If {@code contents} is {@code null}.
		 * @throws UnsupportedOperationException If the {@code ExternalFileHandler} does not support writing files (like if it's read-only).
		 */
		public default void writeProjectFile(byte[] contents) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Finds and returns a {@code List} consisting of all of the files found in the root directory.
		 * This method is used by {@link FinmerProjectReader} in order to discover the assets in a Finmer project.
		 * @return The files in the root directory.
		 */
		public List<String> listFiles();
	}

	/**
	 * The default reading implementation of {@link ExtendedExternalFileHandler}, which reads projects from a directory on the filesystem.
	 * @param projectDirectory The directory containing the project file and its associated assets.
	 * @param projectFile The project file itself.
	 * @author SyntacticKitsune
	 */
	public static record ReadOnlyExternalFileHandler(Path projectDirectory, Path projectFile) implements ExtendedExternalFileHandler {

		/**
		 * Constructs a new {@code ReadOnlyExternalFileHandler}.
		 * @param projectDirectory The directory containing the project file (usually) and its associated assets.
		 * @param projectFile The project file itself. It need not be in the project directory, but this may confuse Finmer's editor.
		 * @throws NullPointerException If {@code projectDirectory} or {@code projectFile} are {@code null}.
		 */
		public ReadOnlyExternalFileHandler {
			Objects.requireNonNull(projectDirectory, "projectDirectory");
			Objects.requireNonNull(projectFile, "projectFile");
		}

		/**
		 * Constructs a new {@code ReadOnlyExternalFileHandler} with the specified project file.
		 * The project directory is assumed to be the enclosing directory of the project file.
		 * @param projectFile The project file.
		 * @return The new {@code ReadOnlyExternalFileHandler}.
		 * @throws NullPointerException If {@code projectFile} is {@code null}.
		 */
		public static ReadOnlyExternalFileHandler forProjectFile(Path projectFile) {
			return new ReadOnlyExternalFileHandler(projectFile.getParent(), projectFile);
		}

		/**
		 * Constructs a new {@code ReadOnlyExternalFileHandler} with the specified project directory.
		 * The project file is assumed to be named after the project directory.
		 * @param projectDirectory The directory containing the project file and its associated assets.
		 * @return The new {@code ReadOnlyExternalFileHandler}.
		 * @throws NullPointerException If {@code projectDirectory} is {@code null}.
		 */
		public static ReadOnlyExternalFileHandler forProjectDirectory(Path projectDirectory) {
			return new ReadOnlyExternalFileHandler(projectDirectory, projectDirectory.resolve(projectDirectory.getFileName().toString() + ".fnproj"));
		}

		@Override
		public List<String> listFiles() {
			try (Stream<Path> stream = Files.list(projectDirectory)) {
				return stream
						.map(path -> path.getFileName().toString())
						.toList();
			} catch (IOException e) {
				return FurblorbUtil.throwAsUnchecked(e);
			}
		}

		@Override
		public byte[] readProjectFile() {
			return readExternalFile(projectFile, projectFile.getFileName().toString());
		}

		@Override
		public byte @Nullable [] readExternalFile(String filename) {
			return readExternalFile(projectDirectory.resolve(filename), filename);
		}

		private byte @Nullable [] readExternalFile(Path file, String filename) {
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