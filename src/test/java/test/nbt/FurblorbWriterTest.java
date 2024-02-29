package test.nbt;

import java.io.IOException;
import java.nio.file.Paths;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

import net.syntactickitsune.furblorb.api.Furball;
import net.syntactickitsune.furblorb.api.io.FurballReader;

import test.TestUtil;

/**
 * Testing for {@link FurblorbWriter}.
 * This isn't a real JUnit test or anything, since these NBT-based "furblorbs" are more of an experiment than anything.
 * @author SyntacticKitsune
 */
public final class FurblorbWriterTest {

	public static void main(String[] args) throws IOException {
		// Place your bets: does this compress better or worse than furballs?
		final byte[] in = TestUtil.readAllBytes("/Core.1.0.1.furball");
		final Furball furball = new FurballReader(in).readFurball();

		final FurblorbWriter writer = new FurblorbWriter().write(furball);
		final CompoundTag tag = writer.codec().unwrap();

		NBTUtil.write(tag, Paths.get("Core.furblorb").toFile(), true);
	}
}