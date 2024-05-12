package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.google.gson.JsonObject;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.FurballMetadata;
import net.syntactickitsune.furblorb.api.asset.FurballAsset;
import net.syntactickitsune.furblorb.api.asset.SceneNode;
import net.syntactickitsune.furblorb.api.io.FinmerProjectReader;
import net.syntactickitsune.furblorb.api.io.FinmerProjectWriter;
import net.syntactickitsune.furblorb.api.io.FurballReader;
import net.syntactickitsune.furblorb.api.io.FurballWriter;
import net.syntactickitsune.furblorb.api.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.api.io.UnsupportedFormatVersionException;
import net.syntactickitsune.furblorb.api.io.impl.BinaryCodec;
import net.syntactickitsune.furblorb.api.io.impl.JsonCodec;
import net.syntactickitsune.furblorb.api.script.visual.expression.BooleanExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.ComparisonExpressionNode;
import net.syntactickitsune.furblorb.api.script.visual.expression.FloatExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.IntExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.LogicalExpression;
import net.syntactickitsune.furblorb.api.script.visual.expression.StringExpression;
import net.syntactickitsune.furblorb.api.script.visual.impl.expression.simple.SimpleExpression;
import net.syntactickitsune.furblorb.api.script.visual.impl.statement.simple.SimpleStatement;
import net.syntactickitsune.furblorb.io.FurballSerializables;

final class FurblorbTest {

	@Test
	void testFormat19Furball() { // Furball → Furball
		doFurball2FurballTest("/Core.1.0.0.furball", (byte) 19, 0, 173);
	}

	@TestFactory
	List<DynamicTest> testFormat19Project() { // Project → Project
		return doProject2ProjectTest("Core.1.0.0.zip", "Core");
	}

	@TestFactory
	List<DynamicTest> testFormat19Furball2Project() { // Furball → Project
		return doFurball2ProjectTest("Core.1.0.0.zip", "/Core.1.0.0.furball", "Core");
	}

	@TestFactory
	List<DynamicTest> testFormat19Project2Furball() { // Project → Furball
		return doProject2FurballTest("Core.1.0.0.zip", "/Core.1.0.0.furball", "Core");
	}

	@Test
	void testFormat20Furball() { // Furball → Furball
		doFurball2FurballTest("/Core.1.0.1.furball", (byte) 20, 0, 175);
	}

	@TestFactory
	List<DynamicTest> testFormat20Project() { // Project → Project
		return doProject2ProjectTest("Core.1.0.1.zip", "Core");
	}

	@TestFactory
	List<DynamicTest> testFormat20Furball2Project() { // Furball → Project
		return doFurball2ProjectTest("Core.1.0.1.zip", "/Core.1.0.1.furball", "Core");
	}

	@TestFactory
	List<DynamicTest> testFormat20Project2Furball() { // Project → Furball
		return doProject2FurballTest("Core.1.0.1.zip", "/Core.1.0.1.furball", "Core");
	}

	@Test
	void testConstructEveryAsset() { // This isn't coverage hacking, probably.
		for (FurballSerializables.Metadata<?> meta : FurballSerializables.lookupAll())
			assertDoesNotThrow(() -> meta.owner().getDeclaredConstructor().newInstance());

		// Extra stragglers:
		assertDoesNotThrow(() -> new BooleanExpression());
		assertDoesNotThrow(() -> new FloatExpression());
		assertDoesNotThrow(() -> new IntExpression());
		assertDoesNotThrow(() -> new StringExpression());
		assertDoesNotThrow(() -> new LogicalExpression());
		assertDoesNotThrow(() -> new SceneNode());
	}

	@TestFactory
	List<DynamicTest> checkSerializableHashCodeAndEqualsImplementations() {
		return FurballSerializables.lookupAll().stream()
				.map(md -> DynamicTest.dynamicTest(md.owner().getSimpleName(), () -> {
					if (ComparisonExpressionNode.class.isAssignableFrom(md.owner())
							|| SimpleExpression.class.isAssignableFrom(md.owner())
							|| SimpleStatement.class.isAssignableFrom(md.owner()))
						return;

					final Method[] methods = md.owner().getDeclaredMethods();
					boolean foundEquals = false;
					boolean foundHashCode = false;

					for (Method m : methods)
						if ("equals".equals(m.getName()) && m.getParameterCount() == 1)
							foundEquals = true;
						else if ("hashCode".equals(m.getName()) && m.getParameterCount() == 0)
							foundHashCode = true;

					assertTrue(foundEquals, () -> "Missing equals() implementation");
					assertTrue(foundHashCode, () -> "Missing hashCode() implementation");
				}))
				.toList();
	}

	@Test
	void testWriteUnsupportedFurball() {
		final Furball furball = new Furball(new FurballMetadata());

		furball.meta.formatVersion = 10;
		assertThrows(UnsupportedFormatVersionException.class, () -> new FurballWriter().write(furball));

		furball.meta.formatVersion = (byte) 100;
		assertThrows(UnsupportedFormatVersionException.class, () -> new FurballWriter().write(furball));
	}

	@Test
	void testReadUnsupportedFurball() {
		final byte[] bytes = new byte[] { 'F', 'U', 'R', 'B', 'A', 'L', 'L', 10 };
		assertThrows(UnsupportedFormatVersionException.class, () -> new FurballReader(bytes).readFurball());

		bytes[bytes.length - 1] = 100;
		assertThrows(UnsupportedFormatVersionException.class, () -> new FurballReader(bytes).readFurball());
	}

	@Test
	void testNotFurball() {
		final byte[] bytes = new byte[] { 0, 0, 0, 0, 0, 0, 0 };
		assertThrows(FurblorbParsingException.class, () -> new FurballReader(bytes).readFurball());
	}

	@Test
	void testReadWriteOnlyCodecs() {
		// Binary
		assertThrows(UnsupportedOperationException.class, () -> new BinaryCodec(false).readBoolean());
		assertThrows(UnsupportedOperationException.class, () -> new BinaryCodec(true).writeBoolean(false));

		// Json
		assertThrows(UnsupportedOperationException.class, () -> new JsonCodec(new JsonObject(), null, false).readBoolean("e"));
		assertThrows(UnsupportedOperationException.class, () -> new JsonCodec(new JsonObject(), null, true).writeBoolean("e", false));
	}

	private static void doFurball2FurballTest(String furballName, byte formatVersion, int dependencyCount, int assetCount) {
		final byte[] in = assertDoesNotThrow(() -> TestUtil.readAllBytes(furballName));
		final Furball furball = assertDoesNotThrow(() -> new FurballReader(in).readFurball());

		assertEquals(formatVersion, furball.meta.formatVersion);
		assertEquals(dependencyCount, furball.dependencies.size());
		assertEquals(assetCount, furball.assets.size());

		final byte[] out = assertDoesNotThrow(() -> new FurballWriter().write(furball).toByteArray());
		assertArrayEquals(in, out);
	}

	private static List<DynamicTest> doProject2ProjectTest(String zip, String module) {
		final Path project = assertDoesNotThrow(() -> TestUtil.extract(zip));
		final MemoryExternalFileHandler in = assertDoesNotThrow(() -> TestUtil.fromZip(module, project));
		final Furball furball = new FinmerProjectReader(in).readFurball();

		final MemoryExternalFileHandler out = new MemoryExternalFileHandler(module);
		new FinmerProjectWriter(out).writeFurball(furball);

		return compareProjects(in.contents(), out.contents());
	}

	private static List<DynamicTest> doFurball2ProjectTest(String zip, String furball, String module) {
		final Path project = assertDoesNotThrow(() -> TestUtil.extract(zip));
		final MemoryExternalFileHandler in = assertDoesNotThrow(() -> TestUtil.fromZip(module, project));

		final byte[] inBytes = assertDoesNotThrow(() -> TestUtil.readAllBytes(furball));
		final Furball inFurball = assertDoesNotThrow(() -> new FurballReader(inBytes).readFurball());

		final MemoryExternalFileHandler out = new MemoryExternalFileHandler(module);
		new FinmerProjectWriter(out).writeFurball(inFurball);

		return compareProjects(in.contents(), out.contents());
	}

	private static List<DynamicTest> doProject2FurballTest(String zip, String furballName, String module) {
		final Path project = assertDoesNotThrow(() -> TestUtil.extract(zip));
		final MemoryExternalFileHandler in = assertDoesNotThrow(() -> TestUtil.fromZip(module, project));
		final Furball inFurball = new FinmerProjectReader(in).readFurball();

		final Furball targetFurball = assertDoesNotThrow(() -> new FurballReader(TestUtil.readAllBytes(furballName)).readFurball());

		Collections.sort(inFurball.assets);
		Collections.sort(targetFurball.assets);

		assertEquals(targetFurball.meta, inFurball.meta);
		assertEquals(targetFurball.dependencies, inFurball.dependencies);
		assertEquals(targetFurball.assets.size(), inFurball.assets.size());

		final List<DynamicTest> ret = new ArrayList<>();

		for (int i = 0; i < targetFurball.assets.size(); i++) {
			final FurballAsset exp = targetFurball.assets.get(i);
			final FurballAsset got = inFurball.assets.get(i);
			ret.add(DynamicTest.dynamicTest(exp.filename, () -> {
				assertEquals(exp, got);
				assertEquals(exp.hashCode(), got.hashCode());
			}));
		}

		final byte[] inBytes = assertDoesNotThrow(() -> new FurballWriter().write(targetFurball).toByteArray());
		final byte[] outBytes = new FurballWriter().write(inFurball).toByteArray();

		ret.add(DynamicTest.dynamicTest("bytes", () -> assertArrayEquals(inBytes, outBytes)));

		return ret;
	}

	private static List<DynamicTest> compareProjects(Map<String, byte[]> inContents, Map<String, byte[]> outContents) {
		return inContents.entrySet()
				.stream()
				.map(entry -> {
					final String name = entry.getKey();
					final byte[] inData = entry.getValue();
					final byte[] outData = outContents.get(name);
					return DynamicTest.dynamicTest(name, () -> {
						if (name.endsWith(".json") || name.endsWith(".lua") || name.endsWith(".fnproj")) {
							final String expected = new String(inData, StandardCharsets.UTF_8).replace("\r\n", "\n");
							final String result = new String(outData, StandardCharsets.UTF_8).replace("\r\n", "\n");
							assertEquals(expected, result);
						} else
							assertArrayEquals(inData, outData);
					});
				})
				.toList();
	}
}