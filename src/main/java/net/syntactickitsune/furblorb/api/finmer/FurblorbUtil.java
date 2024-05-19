package net.syntactickitsune.furblorb.api.finmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * A collection of various utilities used internally by Furblorb.
 * These should not generally be useful to others using this project.
 * @author SyntacticKitsune
 */
@Internal
public final class FurblorbUtil {

	/**
	 * Reads a text file from the Furblorb jar file.
	 * @param path The path to the file in the jar.
	 * @return The contents of the file.
	 */
	public static List<String> readStringResource(String path) {
		final List<String> ret = new ArrayList<>();

		try (InputStream is = FurblorbUtil.class.getResourceAsStream(path);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null)
				ret.add(line);
		} catch (IOException e) {
			throwAsUnchecked(e);
		}

		return ret;
	}

	/**
	 * {@code Finmer.Core.Serialization}'s {@code AssetSerializer.ComputeTypeHash} method, <strike>shamelessly copied</strike> ported to Java.
	 * Given an input string, it returns whatever .NET thinks the hash of it should be (but stable across versions, or so the comment says).
	 * @param input The input string.
	 * @return The {@code int} hash.
	 */
	public static int hash(String input) {
		int hash1 = 5381;
		int hash2 = hash1;

		for (int i = 0; i < input.length(); i += 2) {
			hash1 = ((hash1 << 5) + hash1) ^ input.charAt(i);
			if (i == input.length() - 1)
				break;
			hash2 = ((hash2 << 5) + hash2) ^ input.charAt(i + 1);
		}

		return hash1 + (hash2 * 1566083941);
	}

	/**
	 * Throws the given {@code Throwable}.
	 * @param <E> The exception type.
	 * @param <T> The return type.
	 * @param e The {@code Throwable} to throw.
	 * @return Nothing. This method cannot return. It is marked as returning something for the convenience of writing exception handlers.
	 * @throws E The given {@code Throwable}.
	 */
	public static <E extends Throwable, T> T throwAsUnchecked(Throwable e) throws E {
		throw (E) e;
	}
}