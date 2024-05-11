package test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.io.FinmerProjectReader.ExtendedExternalFileHandler;

final class MemoryExternalFileHandler implements ExtendedExternalFileHandler {

	private final String projectName;
	private final Map<String, byte[]> contents;
	private final boolean read;

	MemoryExternalFileHandler(String projectName, Map<String, byte[]> contents) {
		this.projectName = Objects.requireNonNull(projectName, "projectName");
		this.contents = Objects.requireNonNull(contents, "contents");
		read = true;
	}

	MemoryExternalFileHandler(String projectName) {
		this.projectName = Objects.requireNonNull(projectName, "projectName");
		contents = new LinkedHashMap<>();
		read = false;
	}

	public Map<String, byte[]> contents() {
		return Collections.unmodifiableMap(contents);
	}

	private String projectFile() {
		return projectName + ".fnproj";
	}

	@Override
	public byte[] readProjectFile() {
		return readExternalFile(projectFile());
	}

	@Override
	public void writeProjectFile(byte[] contents) {
		writeExternalFile(projectFile(), contents);
	}

	@Override
	public List<String> listFiles() {
		if (!read) throw new UnsupportedOperationException();
		return List.copyOf(contents.keySet());
	}

	@Override
	public byte @Nullable [] readExternalFile(String filename) {
		if (!read) throw new UnsupportedOperationException();
		return contents.get(Objects.requireNonNull(filename, "filename"));
	}

	@Override
	public void writeExternalFile(String filename, byte[] contents) {
		if (read) throw new UnsupportedOperationException();
		this.contents.put(Objects.requireNonNull(filename, "filename"), Objects.requireNonNull(contents, "contents"));
	}
}