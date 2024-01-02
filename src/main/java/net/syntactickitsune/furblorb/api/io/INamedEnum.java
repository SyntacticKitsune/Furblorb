package net.syntactickitsune.furblorb.api.io;

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
}