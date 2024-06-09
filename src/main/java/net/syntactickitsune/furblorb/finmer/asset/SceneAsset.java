package net.syntactickitsune.furblorb.finmer.asset;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.syntactickitsune.furblorb.finmer.FurballUtil;
import net.syntactickitsune.furblorb.finmer.ISerializableVisitor;
import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.io.FurballSerializables;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.finmer.script.Script;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.FurblorbParsingException;
import net.syntactickitsune.furblorb.io.INamedEnum;

/**
 * <p>
 * A {@code SceneAsset} is a sort of combination of a room and a choice-based tree.
 * Effectively, a {@code SceneAsset} is simultaneously both of these, and collapses
 * into one of these upon observation. (That is, Schrödinger's {@code SceneAsset}.)
 * </p>
 * <p>
 * If you're familiar with Twine, you may be aware of how things are internally represented by
 * a series of "passages" which can link to other passages. You may have also seen approximations
 * of room-based navigation using this system. A {@code SceneAsset} is very reminiscent of that.
 * </p>
 * <p>
 * As always, see also <a href="https://docs.finmer.dev/asset-types/scenes">the documentation</a>,
 * which will likely prove far more enlightening.
 * </p>
 */
@RegisterSerializable("AssetScene")
public final class SceneAsset extends FurballAsset {

	/**
	 * The root node of the scene.
	 */
	public SceneNode root;

	/**
	 * Called a "Custom Script" in the docs, this is a script to glue on top of every single other script in this scene.
	 * This is useful for consolidating duplicate code between scripts in the scene, like checking a condition.
	 */
	@Nullable
	public Script head; // Real @Inject(at = @At("HEAD")) energy here.

	/**
	 * Called an "Enter Script" in the docs, this is a script that runs any time the player "enters" the scene.
	 */
	@Nullable
	public Script onEnter;

	/**
	 * Called a "Leave Script" in the docs, this is a script that runs any time the player "leaves" the scene.
	 */
	@Nullable
	public Script onLeave;

	/**
	 * Whether or not the scene represents a game entrypoint.
	 * If multiple game entrypoints are discovered, the player must choose one when creating a new game.
	 */
	@RequiresFormatVersion(20)
	public boolean gameStart = false;

	/**
	 * Whether or not the scene patches some other scene.
	 */
	public boolean patch = false;

	/**
	 * For patches, determines how the scene is to be injected.
	 */
	public InjectMode injectMode = InjectMode.AFTER_TARGET;

	/**
	 * For patches, determines the scene being patched.
	 */
	public UUID injectionTargetScene = FurballUtil.EMPTY_UUID;

	/**
	 * For patches, determines the node of the scene being patched.
	 * In other words: where to apply the patch.
	 */
	public String injectionTargetNode = "";

	/**
	 * For {@linkplain #gameStart game entrypoints}, the description to show in the selection.
	 */
	@RequiresFormatVersion(20)
	public String gameStartDescription = "";

	/**
	 * Constructs a new {@code SceneAsset} with default values.
	 */
	public SceneAsset() {}

	/**
	 * Decodes a {@code SceneAsset} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public SceneAsset(Decoder in) {
		super(in);

		head = in.readOptionalObject("ScriptCustom", FurballSerializables::read);
		onEnter = in.readOptionalObject("ScriptEnter", FurballSerializables::read);
		onLeave = in.readOptionalObject("ScriptLeave", FurballSerializables::read);

		if (in.formatVersion() >= 20) {
			gameStart = in.readBoolean("IsGameStart");
			if (gameStart)
				gameStartDescription = in.readString("GameStartDescription");
			else
				in.assertDoesNotExist("GameStartDescription", "IsGameStart must be enabled first");
		} else {
			in.assertDoesNotExist("IsGameStart", "unsupported in format version <20");
			in.assertDoesNotExist("GameStartDescription", "unsupported in format version <20");
		}

		patch = in.readBoolean("IsPatch");
		if (patch) {
			injectMode = in.readEnum("InjectMode", InjectMode.class);
			injectionTargetScene = in.readUUID("InjectTargetScene");
			injectionTargetNode = in.readString("InjectTargetNode");

			if (in.validate()) {
				if (head != null) throw new FurblorbParsingException("ScriptCustom not allowed in patches");
				if (onEnter != null) throw new FurblorbParsingException("ScriptEnter not allowed in patches");
				if (onLeave != null) throw new FurblorbParsingException("ScriptLeave not allowed in patches");
			}
		} else {
			in.assertDoesNotExist("InjectMode", "not a patch");
			in.assertDoesNotExist("InjectTargetScene", "not a patch");
			in.assertDoesNotExist("InjectTargetNode", "not a patch");
		}

		try {
			root = in.readObject("Root", SceneNode::new);
		} catch (SceneNode.CascadingException e) {
			e.path.add(0, filename);
			throw e;
		}
	}

	@Override
	protected void write0(Encoder to) {
		to.writeOptionalObject("ScriptCustom", head, Script::writeWithId);
		to.writeOptionalObject("ScriptEnter", onEnter, Script::writeWithId);
		to.writeOptionalObject("ScriptLeave", onLeave, Script::writeWithId);

		if (to.formatVersion() >= 20) {
			to.writeBoolean("IsGameStart", gameStart);
			if (gameStart)
				to.writeString("GameStartDescription", gameStartDescription);
			else
				to.assertDoesNotExist("GameStartDescription", gameStartDescription.isEmpty() ? null : "", "IsGameStart must be enabled first");
		}

		to.writeBoolean("IsPatch", patch);
		if (patch) {
			to.writeEnum("InjectMode", injectMode);
			to.writeUUID("InjectTargetScene", injectionTargetScene);
			to.writeString("InjectTargetNode", injectionTargetNode);

			if (to.validate()) {
				if (head != null) throw new FurblorbParsingException("ScriptCustom not allowed in patches");
				if (onEnter != null) throw new FurblorbParsingException("ScriptEnter not allowed in patches");
				if (onLeave != null) throw new FurblorbParsingException("ScriptLeave not allowed in patches");
			}
		}

		to.writeObject("Root", root, SceneNode::write);
	}

	@Override
	public void visit(ISerializableVisitor visitor) {
		if (visitor.visitAsset(this)) {
			visitor.visitText(gameStartDescription);
			if (head != null) head.visit(visitor);
			if (onEnter != null) onEnter.visit(visitor);
			if (onLeave != null) onLeave.visit(visitor);
			root.visit(visitor);

			visitor.visitEnd();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SceneAsset a)) return false;
		return id.equals(a.id) && Objects.equals(filename, a.filename) && injectMode == a.injectMode && patch == a.patch
				&& gameStart == a.gameStart && Objects.equals(gameStartDescription, a.gameStartDescription)
				&& Objects.equals(injectionTargetScene, a.injectionTargetScene) && Objects.equals(injectionTargetNode, a.injectionTargetNode)
				&& Objects.equals(onLeave, a.onLeave) && Objects.equals(onEnter, a.onEnter) && Objects.equals(head, a.head)
				&& Objects.equals(root, a.root);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, filename, root, head, onEnter, onLeave,
				gameStart, patch, injectMode, injectionTargetScene,
				injectionTargetNode, gameStartDescription);
	}

	/**
	 * Represents the different ways a patch may be performed.
	 * Specifically, this outlines different injection points a patch may make use of.
	 */
	public static enum InjectMode implements INamedEnum {

		/**
		 * Insert immediately before the target.
		 */
		BEFORE_TARGET("BeforeTarget"), // @Inject(at = @At("HEAD"))

		/**
		 * Insert immediately after the target.
		 */
		AFTER_TARGET("AfterTarget"), // @Inject(at = @At("TAIL"))

		/**
		 * Insert inside the target, at the top.
		 */
		INSIDE_AT_START("InsideAtStart"), // @Inject(at = @At(value = "INVOKE", ...))

		/**
		 * Insert inside the target, at the end.
		 */
		INSIDE_AT_END("InsideAtEnd"); // @Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, ...))

		private final String id;

		private InjectMode(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}