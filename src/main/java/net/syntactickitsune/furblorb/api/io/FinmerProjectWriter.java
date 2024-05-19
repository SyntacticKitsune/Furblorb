package net.syntactickitsune.furblorb.api.io;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.syntactickitsune.furblorb.FurblorbUtil;
import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballDependency;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.io.FinmerProjectReader.ExtendedExternalFileHandler;
import net.syntactickitsune.furblorb.api.io.impl.JsonCodec;

/**
 * <p>
 * The {@code FinmerProjectWriter} {@code class} creates Finmer projects from {@link Furball Furballs}.
 * That is, it writes every asset, script, and image to a provided directory.
 * </p>
 * <p>
 * Using this class is as simple as:
 * <code><pre>
 * new FinmerProjectWriter(WriteOnlyExternalFileHandler.fromProjectFile(projectFile)).writeFurball(furball);</pre></code>
 * </p>
 * @author SyntacticKitsune
 * @see FinmerProjectReader
 */
public final class FinmerProjectWriter {

	static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	private final ExtendedExternalFileHandler externalFiles;

	/**
	 * Constructs a new {@code FinmerProjectWriter} with the specified external file handler.
	 * The file handler will handle writing all of the required files.
	 * @param externalFiles The external file handler.
	 * @throws NullPointerException If {@code externalFiles} is {@code null}.
	 */
	public FinmerProjectWriter(ExtendedExternalFileHandler externalFiles) {
		this.externalFiles = Objects.requireNonNull(externalFiles, "externalFiles");
	}

	private byte[] toBytes(JsonObject json) {
		final String j = externalFiles.normalizeLineEndings(toJson(json));
		// Unfortunately, we must insert a UTF-8 BOM character.
		// I swear I'm going to throw whoever thought UTF-8 BOM was a good idea.
		final char bomChar = (char) 65279;

		return (bomChar + j).getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Converts the specified {@code JsonElement} into a {@code String} with tab indention.
	 * @param elem The element to convert.
	 * @return The converted {@code String}.
	 */
	@Internal
	public static String toJson(JsonElement elem) {
		final StringWriter sw = new StringWriter();

		try (JsonWriter jw = new JsonWriter(sw)) {
			jw.setIndent("\t");
			GSON.toJson(elem, jw);
		} catch (IOException e) {
			FurblorbUtil.throwAsUnchecked(e);
		}

		return sw.toString();
	}

	private void writeProjectFile(Furball furball) {
		final JsonCodec codec = new JsonCodec(externalFiles, furball.meta.formatVersion);

		furball.meta.write(codec);

		codec.writeList("Dependencies", furball.dependencies, FurballDependency::write);

		externalFiles.writeProjectFile(toBytes(codec.unwrap()));
	}

	/**
	 * Writes the specified {@code Furball} to the {@code FinmerProjectWriter}'s project directory.
	 * @param furball The furball to write.
	 * @throws UnsupportedFormatVersionException If the furball has a format version that cannot be written by this {@code FinmerProjectWriter}.
	 * @throws NullPointerException If {@code furball} is {@code null}.
	 */
	public void writeFurball(Furball furball) {
		FurballWriter.checkFormatVersion(furball.meta.formatVersion);

		writeProjectFile(furball);

		for (FurballAsset asset : furball.assets) {
			final JsonCodec codec = new JsonCodec(externalFiles, furball.meta.formatVersion);
			asset.writeWithId(codec);
			externalFiles.writeExternalFile(asset.filename + ".json", toBytes(codec.unwrap()));
		}
	}

	/**
	 * The default writing implementation of {@link ExtendedExternalFileHandler}, which writes projects to a directory on the filesystem.
	 * @param projectDirectory The directory containing the project file (usually) and its associated assets.
	 * @param projectFile The project file itself. It need not be in the project directory, but this may confuse Finmer's editor.
	 * @author SyntacticKitsune
	 */
	public static record WriteOnlyExternalFileHandler(Path projectDirectory, Path projectFile) implements ExtendedExternalFileHandler {

		/**
		 * Constructs a new {@code WriteOnlyExternalFileHandler}.
		 * @param projectDirectory The directory containing the project file (usually) and its associated assets.
		 * @param projectFile The project file itself. It need not be in the project directory, but this may confuse Finmer's editor.
		 * @throws NullPointerException If {@code projectDirectory} or {@code projectFile} are {@code null}.
		 */
		public WriteOnlyExternalFileHandler {
			Objects.requireNonNull(projectDirectory, "projectDirectory");
			Objects.requireNonNull(projectFile, "projectFile");
		}

		/**
		 * Constructs a new {@code WriteOnlyExternalFileHandler} with the specified project file.
		 * The project directory is assumed to be the enclosing directory of the project file.
		 * @param projectFile The project file.
		 * @return The new {@code WriteOnlyExternalFileHandler}.
		 * @throws NullPointerException If {@code projectFile} is {@code null}.
		 */
		public static WriteOnlyExternalFileHandler forProjectFile(Path projectFile) {
			return new WriteOnlyExternalFileHandler(projectFile.getParent(), projectFile);
		}

		/**
		 * Constructs a new {@code WriteOnlyExternalFileHandler} with the specified project directory.
		 * The project file is assumed to be named after the project directory.
		 * @param projectDirectory The directory containing the project file and its associated assets.
		 * @return The new {@code WriteOnlyExternalFileHandler}.
		 * @throws NullPointerException If {@code projectDirectory} is {@code null}.
		 */
		public static WriteOnlyExternalFileHandler forProjectDirectory(Path projectDirectory) {
			return new WriteOnlyExternalFileHandler(projectDirectory, projectDirectory.resolve(projectDirectory.getFileName().toString() + ".fnproj"));
		}

		@Override
		public List<String> listFiles() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeProjectFile(byte[] contents) {
			writeExternalFile(projectFile, contents);
		}

		@Override
		public void writeExternalFile(String filename, byte[] contents) {
			Objects.requireNonNull(filename, "filename");
			Objects.requireNonNull(contents, "contents");

			final Path to = projectDirectory.resolve(filename);
			writeExternalFile(to, contents);
		}

		private static void writeExternalFile(Path to, byte[] contents) {
			try {
				Files.createDirectories(to.getParent());
				Files.write(to, contents, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException e) {
				FurblorbUtil.throwAsUnchecked(e);
			}
		}
	}
}