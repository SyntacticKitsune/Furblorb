package net.syntactickitsune.furblorb.api.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.api.FurblorbException;
import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;
import net.syntactickitsune.furblorb.api.script.Script;
import net.syntactickitsune.furblorb.api.util.FurballUtil;
import net.syntactickitsune.furblorb.io.FurballSerializables;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

/**
 * Represents an individual node in a {@code SceneAsset}'s node tree.
 */
@RegisterSerializable(value = "SceneNode", since = 20) // TODO: May not be the right class name.
public final class SceneNode {

	public Type type;
	public String key = "";

	public String title = "";
	public String tooltip = "";
	public boolean highlight = false;
	public float buttonWidth = 1;

	public Direction compassLink = Direction.NORTH;
	public UUID compassTarget = FurballUtil.EMPTY_UUID;

	public String linkTarget = "";

	@Nullable
	public Script onTrigger;
	@Nullable
	public Script displayTest; // Don't forget to register your stuff using ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DisplayTest.class, () -> new DisplayTest(...));

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
				in.assertDoesNotExist("ScriptAction", "unsupported for " + type.id + " nodes.");
				in.assertDoesNotExist("ScriptAppear", "unsupported for " + type.id + " nodes.");
			}

			if (children)
				this.children.addAll(in.readList("Children", SceneNode::new));
			else
				in.assertDoesNotExist("Children", type.id + " nodes may not have children.");
		} catch (CascadingException e) {
			e.path.add(0, key);
			throw e;
		} catch (Exception e) {
			throw new CascadingException(key, e);
		}
	}

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
		}

		if (children) // Recursion :concern:
			to.writeList("Children", this.children, SceneNode::write);
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

	public static enum Type implements INamedEnum {

		ROOT("Root"),
		STATE("State"),
		CHOICE("Choice"),
		LINK("Link"),
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

	public static enum Direction implements INamedEnum { // What kind of person goes counter-clockwise‽‽

		NORTH("North"),
		WEST("West"),
		SOUTH("South"),
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

	public static final class CascadingException extends FurblorbException {

		final List<String> path = new ArrayList<>();

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