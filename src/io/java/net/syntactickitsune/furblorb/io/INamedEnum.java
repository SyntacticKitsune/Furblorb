package net.syntactickitsune.furblorb.io;

/**
 * Represents an {@code enum} with named constants. (Yes, this sounds redundant.)
 * Its current use is to track the C# name of each constant, which is relevant for reading/writing Finmer projects.
 * @author SyntacticKitsune
 * @see Encoder#writeEnum(String, Enum)
 * @see Decoder#readEnum(String, Class)
 */
@SuppressWarnings("javadoc")
public interface INamedEnum {

	/**
	 * @return The id of the constant.
	 */
	public String id();

	/**
	 * Returns the earliest format version this enum constant is available in.
	 * Decoders/encoders will pretend the constant doesn't exist on older format versions.
	 * @return The format version of the constant.
	 */
	public default byte formatVersion() {
		return 0;
	}
}