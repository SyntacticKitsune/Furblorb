package net.syntactickitsune.furblorb.finmer.asset.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.FurballUtil;
import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.asset.SceneAsset;
import net.syntactickitsune.furblorb.finmer.asset.scene.patch.ScenePatch;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.IFurballSerializable;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.Script;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;

/**
 * Represents an individual node in a {@code SceneAsset}'s node tree.
 */
public final class SceneNode implements IFurballSerializable {

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
	 * The patch of a {@linkplain Type#PATCH patch node}.
	 */
	@Nullable
	@RequiresFormatVersion(21)
	public ScenePatch patch;

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
		key = type.properties.contains(Properties.KEY) || in.formatVersion() < 21 ? in.readString("Key") : "";

		Set<Properties> props = type.properties;

		try {
			switch (type) {
				case CHOICE -> {
					title = in.readString("Title");
					tooltip = in.readString("Tooltip");
					highlight = in.readBoolean("Highlight");
					buttonWidth = in.readFloat("ButtonWidth");
				}
				case LINK -> {
					linkTarget = in.readString("LinkTarget");
				}
				case COMPASS -> {
					compassLink = in.readEnum("CompassLinkDirection", Direction.class);
					compassTarget = in.readUUID("CompassLinkScene");
				}
				case PATCH -> {
					patch = in.readObject("PatchData", FurballSerializables::read);
					props = EnumSet.copyOf(props);
					props.addAll(patch.getAdditionalProperties());
				}
				default -> {}
			}

			if (props.contains(Properties.SCRIPTS)) {
				onTrigger = in.readOptionalObject("ScriptAction", FurballSerializables::read);
				displayTest = in.readOptionalObject("ScriptAppear", FurballSerializables::read);
			} else { // TODO: These will probably be misleading for patches
				in.assertDoesNotExist("ScriptAction", "unsupported for " + type.id + " nodes");
				in.assertDoesNotExist("ScriptAppear", "unsupported for " + type.id + " nodes");
			}

			if (props.contains(Properties.CHILDREN))
				this.children.addAll(in.readObjectList("Children", SceneNode::new));
			else
				in.assertDoesNotExist("Children", type.id + " nodes may not have children");
		} catch (CascadingSceneLoadingException e) {
			e.path.add(0, key);
			throw e;
		} catch (Exception e) {
			throw new CascadingSceneLoadingException(key, e);
		}
	}

	/**
	 * Writes this {@code SceneNode} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	@Override
	public void write(Encoder to) {
		to.writeEnum("NodeType", type);

		if (type.properties.contains(Properties.KEY) || to.formatVersion() < 21)
			to.writeString("Key", key);

		Set<Properties> props = type.properties;

		switch (type) {
			case CHOICE -> {
				to.writeString("Title", title);
				to.writeString("Tooltip", tooltip);
				to.writeBoolean("Highlight", highlight);
				to.writeFloat("ButtonWidth", buttonWidth);
			}
			case LINK -> {
				to.writeString("LinkTarget", linkTarget);
			}
			case COMPASS -> {
				to.writeEnum("CompassLinkDirection", compassLink);
				to.writeUUID("CompassLinkScene", compassTarget);
			}
			case PATCH -> {
				to.writeObject("PatchData", patch, ScenePatch::writeWithId);
				props = EnumSet.copyOf(props);
				props.addAll(patch.getAdditionalProperties());
			}
			default -> {}
		}

		if (props.contains(Properties.SCRIPTS)) {
			to.writeOptionalObject("ScriptAction", onTrigger, Script::writeWithId);
			to.writeOptionalObject("ScriptAppear", displayTest, Script::writeWithId);
		} else {
			to.assertDoesNotExist("ScriptAction", onTrigger, "unsupported for " + type.id + " nodes");
			to.assertDoesNotExist("ScriptAppear", displayTest, "unsupported for " + type.id + " nodes");
		}

		if (props.contains(Properties.CHILDREN)) // Recursion :concern:
			to.writeObjectList("Children", this.children, SceneNode::write);
		else
			to.assertDoesNotExist("Children", this.children.isEmpty() ? null : this.children, type.id + " nodes may not have children");
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitSerializable(this)) {
			if (type == Type.CHOICE) {
				visitor.visitText(title);
				visitor.visitText(tooltip);
			}
			if (onTrigger != null) onTrigger.visit(visitor);
			if (displayTest != null) displayTest.visit(visitor);
			for (SceneNode child : children)
				child.visit(visitor);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SceneNode a)) return false;
		return type == a.type && highlight == a.highlight && buttonWidth == a.buttonWidth && compassLink == a.compassLink
				&& Objects.equals(key, a.key) && Objects.equals(title, a.title) && Objects.equals(tooltip, a.tooltip)
				&& Objects.equals(compassTarget, a.compassTarget) && Objects.equals(linkTarget, a.linkTarget)
				&& Objects.equals(onTrigger, a.onTrigger) && Objects.equals(displayTest, a.displayTest)
				&& Objects.equals(patch, a.patch) && Objects.equals(children, a.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, key, title, tooltip, highlight, buttonWidth, compassLink, compassTarget, linkTarget, onTrigger, displayTest, patch, children);
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
		ROOT("Root", Properties.CHILDREN),

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
		STATE("State", Properties.KEY, Properties.CHILDREN, Properties.SCRIPTS),

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
		CHOICE("Choice", Properties.KEY, Properties.CHILDREN, Properties.SCRIPTS),

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
		COMPASS("Compass", Properties.KEY, Properties.SCRIPTS),

		@RequiresFormatVersion(21)
		PATCH("Patch", Properties.KEY);

		private final String id;
		public final Set<Properties> properties;

		private Type(String id, Properties... props) {
			this.id = id;
			this.properties = props.length == 0 ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(props)));
		}

		@Override
		public String id() {
			return id;
		}

		@Override
		public byte formatVersion() {
			return this == PATCH ? (byte) 21 : 0;
		}
	}

	public static enum Properties {

		KEY,
		CHILDREN,
		SCRIPTS;
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
}