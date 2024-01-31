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
 * new FinmerProjectWriter(projectDirectory).writeFurball(furball);</pre></code>
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

	/**
	 * Constructs a new {@code FinmerProjectWriter} with the specified Finmer project directory.
	 * @param projectDirectory The project directory to write to.
	 * @throws NullPointerException If {@code projectDirectory} is {@code null}.
	 */
	public FinmerProjectWriter(Path projectDirectory) {
		this(new DefaultExternalFileHandler(projectDirectory));
	}

	private void writeJson(JsonObject json, String to) {
		final String j = externalFiles.normalizeLineEndings(toJson(json));
		// Unfortunately, we must insert a UTF-8 BOM character.
		// I swear I'm going to throw whoever thought UTF-8 BOM was a good idea.
		final char bomChar = (char) 65279;

		externalFiles.writeExternalFile(to, (bomChar + j).getBytes(StandardCharsets.UTF_8));
	}

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

		writeJson(codec.unwrap(), externalFiles.projectFilename());
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
			writeJson(codec.unwrap(), asset.filename + ".json");
		}
	}

	/**
	 * The default writing implementation of {@link ExtendedExternalFileHandler}, which writes projects to a directory on the filesystem.
	 * @param projectDirectory The project directory to write to.
	 * @author SyntacticKitsune
	 */
	public static record DefaultExternalFileHandler(Path projectDirectory) implements ExtendedExternalFileHandler {

		/**
		 * Constructs a new {@code DefaultExternalFileHandler}.
		 * @param projectDirectory The project directory to write to.
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
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeExternalFile(String filename, byte[] contents) {
			Objects.requireNonNull(filename, "filename");
			Objects.requireNonNull(contents, "contents");

			try {
				final Path to = projectDirectory.resolve(filename);
				Files.createDirectories(to.getParent());
				Files.write(to, contents, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException e) {
				FurblorbUtil.throwAsUnchecked(e);
			}
		}
	}
}