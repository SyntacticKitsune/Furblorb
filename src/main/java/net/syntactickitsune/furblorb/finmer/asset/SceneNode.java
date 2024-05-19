package net.syntactickitsune.furblorb.finmer.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.FurballUtil;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.Script;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbException;
import net.syntactickitsune.furblorb.io.INamedEnum;

/**
 * Represents an individual node in a {@code SceneAsset}'s node tree.
 */
@RegisterSerializable(value = "SceneNode", since = 20) // TODO: May not be the right class name, since Finmer's SceneNode is contained within AssetScene.
public final class SceneNode {

	/**
	 * Identifies the kind of node, and in particular what fields it has available to it.
	 */
	public Type type;

	/**
	 * Identifies the node itself, for referencing by {@linkplain Type#LINK link nodes} or patches.
	 */
	public String key = "";

	/**
	 * The title of {@linkplain Type#CHOICE choice nodes}.
	 * Specifically, the text displayed on the button.
	 */
	public String title = "";

	/**
	 * The tooltip of {@linkplain Type#CHOICE choice nodes}.
	 * Specifically, the text displayed above the button when hovering over it.
	 */
	public String tooltip = "";

	/**
	 * Whether to "visually accentuate" a {@linkplain Type#CHOICE choice node}'s button.
	 * (The asset documentation doesn't mention this option and the field's documentation is just as unenlightening.)
	 */
	public boolean highlight = false;

	/**
	 * The size of a {@linkplain Type#CHOICE choice node}'s button, as a multiplier of its normal size.
	 */
	public float buttonWidth = 1;

	/**
	 * Defines the directional button of a {@linkplain Type#COMPASS compass node}.
	 */
	public Direction compassLink = Direction.NORTH;

	/**
	 * The target scene of a {@linkplain Type#COMPASS compass node}.
	 */
	public UUID compassTarget = FurballUtil.EMPTY_UUID;

	/**
	 * The target node of a {@linkplain Type#LINK link node}.
	 */
	public String linkTarget = "";

	/**
	 * <p>
	 * A script invoked when the scene node is "triggered."
	 * For {@linkplain Type#COMPASS compass} and {@linkplain Type#CHOICE choice} nodes, this is when the button is clicked.
	 * For {@linkplain Type#STATE state} nodes, this is when the state is reached.
	 * </p>
	 * <p>
	 * Finmer's documentation (and the editor) call this "Actions Taken".
	 * </p>
	 */
	@Nullable
	public Script onTrigger;

	/**
	 * <p>
	 * A script invoked to determine the eligibility of a {@linkplain Type#CHOICE choice} or {@linkplain Type#STATE state}.
	 * For choice nodes, the script determines whether the choice is visible.
	 * For state nodes, the script determines whether the state is visited.
	 * </p>
	 * <p>
	 * Finmer's documentation (and the editor) call this "Appears When".
	 * </p>
	 */
	@Nullable
	public Script displayTest; // Don't forget to register your stuff using ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DisplayTest.class, () -> new DisplayTest(...));

	/**
	 * A list of child nodes.
	 * Only {@linkplain Type#ROOT root}, {@linkplain Type#STATE state}, and {@linkplain Type#CHOICE choice} may have children.
	 */
	public final List<SceneNode> children = new ArrayList<>();

	/**
	 * Constructs a new {@code SceneNode} with default values.
	 */
	public SceneNode() {}

	/**
	 * Decodes a {@code SceneNode} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SceneNode(Decoder in) {
		type = in.readEnum("NodeType", Type.class);
		key = in.readString("Key");

		try {
			boolean scripts = false;
			boolean children = false;

			switch (type) {
				case CHOICE -> {
					title = in.readString("Title");
					tooltip = in.readString("Tooltip");
					highlight = in.readBoolean("Highlight");
					buttonWidth = in.readFloat("ButtonWidth");
					scripts = children = true;
				}
				case STATE -> scripts = children = true;
				case ROOT -> children = true;
				case LINK -> {
					linkTarget = in.readString("LinkTarget");
				}
				case COMPASS -> {
					compassLink = in.readEnum("CompassLinkDirection", Direction.class);
					compassTarget = in.readUUID("CompassLinkScene");
					scripts = true;
				}
			}

			if (scripts) {
				onTrigger = in.readOptional("ScriptAction", FurballSerializables::read);
				displayTest = in.readOptional("ScriptAppear", FurballSerializables::read);
			} else {
				in.assertDoesNotExist("ScriptAction", "unsupported for " + type.id + " nodes");
				in.assertDoesNotExist("ScriptAppear", "unsupported for " + type.id + " nodes");
			}

			if (children)
				this.children.addAll(in.readList("Children", SceneNode::new));
			else
				in.assertDoesNotExist("Children", type.id + " nodes may not have children");
		} catch (CascadingException e) {
			e.path.add(0, key);
			throw e;
		} catch (Exception e) {
			throw new CascadingException(key, e);
		}
	}

	/**
	 * Writes this {@code SceneNode} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeEnum("NodeType", type);
		to.writeString("Key", key);

		boolean scripts = false;
		boolean children = false;

		switch (type) {
			case CHOICE -> {
				to.writeString("Title", title);
				to.writeString("Tooltip", tooltip);
				to.writeBoolean("Highlight", highlight);
				to.writeFloat("ButtonWidth", buttonWidth);
				scripts = children = true;
			}
			case STATE -> scripts = children = true;
			case ROOT -> children = true;
			case LINK -> {
				to.writeString("LinkTarget", linkTarget);
			}
			case COMPASS -> {
				to.writeEnum("CompassLinkDirection", compassLink);
				to.writeUUID("CompassLinkScene", compassTarget);
				scripts = true;
			}
		}

		if (scripts) {
			to.writeOptional("ScriptAction", onTrigger, Script::writeWithId);
			to.writeOptional("ScriptAppear", displayTest, Script::writeWithId);
		} else {
			to.assertDoesNotExist("ScriptAction", onTrigger, "unsupported for " + type.id + " nodes");
			to.assertDoesNotExist("ScriptAppear", displayTest, "unsupported for " + type.id + " nodes");
		}

		if (children) // Recursion :concern:
			to.writeList("Children", this.children, SceneNode::write);
		else
			to.assertDoesNotExist("Children", this.children.isEmpty() ? null : this.children, type.id + " nodes may not have children");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SceneNode a)) return false;
		return type == a.type && highlight == a.highlight && buttonWidth == a.buttonWidth && compassLink == a.compassLink
				&& Objects.equals(key, a.key) && Objects.equals(title, a.title) && Objects.equals(tooltip, a.tooltip)
				&& Objects.equals(compassTarget, a.compassTarget) && Objects.equals(linkTarget, a.linkTarget)
				&& Objects.equals(onTrigger, a.onTrigger) && Objects.equals(displayTest, a.displayTest)
				&& Objects.equals(children, a.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, key, title, tooltip, highlight, buttonWidth, compassLink, compassTarget, linkTarget, onTrigger, displayTest, children);
	}

	/**
	 * Represents the different types a {@linkplain SceneNode scene node} may be.
	 */
	public static enum Type implements INamedEnum {

		/**
		 * <p>
		 * Represents the root {@linkplain SceneNode scene node} of a {@link SceneAsset}.
		 * Root nodes are treated like {@linkplain #CHOICE choice nodes} by the game, meaning that one cannot place choice nodes directly inside a root node.
		 * </p>
		 * <p>
		 * Root nodes may contain children, but may not contain scripts.
		 * </p>
		 */
		ROOT("Root"),

		/**
		 * <p>
		 * Represents a "state" {@linkplain SceneNode scene node}.
		 * </p>
		 * <p>
		 * State nodes represent a branch in the dialogue tree, and frequently contain choices.
		 * For those who have experience with Twine, these are (in a sense) similar to passages.
		 * </p>
		 * <p>
		 * State nodes may contain both children and scripts.
		 * </p>
		 */
		STATE("State"),

		/**
		 * <p>
		 * Represents a choice {@linkplain SceneNode scene node}.
		 * </p>
		 * <p>
		 * Choice nodes represent options the player may choose to change the game state in some way.
		 * (For example, letting the player ask a predefined question in dialogue.)
		 * Choice nodes are presented to the player as buttons with titles and tooltips.
		 * </p>
		 * <p>
		 * Choice nodes may contain both children ({@linkplain #STATE state nodes} or {@linkplain #LINK link nodes}) and scripts.
		 * </p>
		 */
		CHOICE("Choice"),

		/**
		 * <p>
		 * Represents a link {@linkplain SceneNode scene node}.
		 * </p>
		 * <p>
		 * Link nodes are a way of "duplicating" part of the scene tree.
		 * In essence, they replace themselves with the node it points to.
		 * This is reminiscent of pointers or {@code GOTO}/{@code JUMP} statements,
		 * </p>
		 * <p>
		 * Link nodes may not contain children nor scripts.
		 * </p>
		 */
		LINK("Link"),

		/**
		 * <p>
		 * Represents a "compass" {@linkplain SceneNode scene node}.
		 * </p>
		 * <p>
		 * Compass nodes are similar to {@linkplain #CHOICE choices}, but may only change the current scene <i>or</i> run a script.
		 * Compass nodes are presented as directional buttons to the left of the "log" or console.
		 * </p>
		 * <p>
		 * Compass nodes may contain scripts, but not children.
		 * </p>
		 */
		COMPASS("Compass");

		private final String id;

		private Type(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}

	/**
	 * Represents the different directions available for {@link Type#COMPASS COMPASS} {@linkplain SceneNode scene nodes}.
	 */
	public static enum Direction implements INamedEnum {

		/**
		 * The north direction, which corresponds to the upper directional button.
		 */
		NORTH("North"),

		/**
		 * The west direction, which corresponds to the left directional button.
		 */
		WEST("West"),

		/**
		 * The south direction, which corresponds to the lower directional button.
		 */
		SOUTH("South"),

		/**
		 * The east direction, which corresponds to the right directional button.
		 */
		EAST("East");

		private final String id;

		private Direction(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}

	/**
	 * A wrapper for exceptions thrown when deserializing {@link SceneNode SceneNodes} that tracks the path to the problematic node.
	 * This makes it easier to diagnose scene deserialization errors.
	 * @author SyntacticKitsune
	 */
	public static final class CascadingException extends FurblorbException {

		final List<String> path = new ArrayList<>();

		/**
		 * Constructs a {@code CascadingException} with the specified values.
		 * @param mostRecent The name of the scene node that is catching the exception.
		 * @param cause The exception in question.
		 */
		public CascadingException(String mostRecent, Throwable cause) {
			super(cause);
			path.add(mostRecent);
		}

		@Override
		public String getMessage() {
			return "Exception reading " + String.join("→", path);
		}
	}
}