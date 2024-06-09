package net.syntactickitsune.furblorb.finmer;

import net.syntactickitsune.furblorb.finmer.asset.FurballAsset;
import net.syntactickitsune.furblorb.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.finmer.script.visual.ScriptNode;

/**
 * Represents a visitor for traversing assets and their contents.
 * @author SyntacticKitsune
 */
public interface ISerializableVisitor {

	/**
	 * A callback for visiting an {@link IFurballSerializable}.
	 * @param serializable The {@link IFurballSerializable} being visited.
	 * @return {@code true} to visit the contents of the serializable, or {@code false} to skip them.
	 */
	public default boolean visitSerializable(IFurballSerializable serializable) {
		return true;
	}

	/**
	 * A callback for visiting an asset.
	 * @param asset The asset being visited.
	 * @return {@code true} to visit the contents of the asset, or {@code false} to skip them.
	 */
	public default boolean visitAsset(FurballAsset asset) {
		return true;
	}

	/**
	 * A callback for visiting Lua code written using the visual scripting framework.
	 * @param node The visual scripting node being visited.
	 * @return {@code true} to visit the contents of the node, or {@code false} to skip them.
	 */
	public default boolean visitVisualCode(ScriptNode node) {
		return true;
	}

	/**
	 * A callback for visiting Lua code.
	 * @param code The code being visited.
	 */
	public default void visitCode(String code) {}

	/**
	 * A callback for visiting the textual content of something.
	 * This method specifically includes English (or some other natural language) text, and not scripts or fields like item names.
	 * @param text The text being visited.
	 */
	public default void visitText(String text) {}

	/**
	 * A callback for visiting the end of a {@linkplain #visitSerializable(IFurballSerializable) serializable}.
	 */
	public default void visitEnd() {}
}