package test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.FurblorbUtil;

public final class TestUtil {

	public static byte[] readAllBytes(String path) throws IOException {
		try (InputStream is = TestUtil.class.getResourceAsStream(path);
				BufferedInputStream bis = new BufferedInputStream(is)) {
			return bis.readAllBytes();
		}
	}

	private static final Path EXTRACTION_DIR = Paths.get("build", "tmp", "jar-extraction");

	static Path extract(String filename) throws IOException {
		Files.createDirectories(EXTRACTION_DIR);

		final Path to = EXTRACTION_DIR.resolve(filename);

		try (InputStream is = TestUtil.class.getResourceAsStream("/" + filename);
				OutputStream os = Files.newOutputStream(to, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			is.transferTo(os);
		}

		return to;
	}

	static MemoryExternalFileHandler fromZip(@Nullable String projectName, Path zipFile) throws IOException {
		try (FileSystem fs = FileSystems.newFileSystem(zipFile)) {
			final Map<String, byte[]> files = new LinkedHashMap<>();

			boolean first = true;
			for (Path root : fs.getRootDirectories()) {
				if (!first) throw new IllegalStateException("Unexpected secondary fs root");
				first = false;

				try (Stream<Path> stream = Files.list(root)) {
					stream.forEachOrdered(p -> {
						try {
							files.put(p.toString().substring(1), Files.readAllBytes(p));
						} catch (IOException e) {
							FurblorbUtil.throwAsUnchecked(e);
						}
					});
				}
			}

			if (projectName == null) {
				projectName = zipFile.getFileName().toString();
				if (projectName.endsWith(".zip"))
					projectName = projectName.substring(0, projectName.length() - ".zip".length());
			}

			return new MemoryExternalFileHandler(projectName, files);
		}
	}
}