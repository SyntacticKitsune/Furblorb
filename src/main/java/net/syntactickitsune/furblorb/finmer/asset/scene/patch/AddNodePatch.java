package net.syntactickitsune.furblorb.finmer.asset.scene.patch;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import net.syntactickitsune.furblorb.finmer.RequiresFormatVersion;
import net.syntactickitsune.furblorb.finmer.asset.scene.SceneNode.Properties;
import net.syntactickitsune.furblorb.finmer.io.RegisterSerializable;
import net.syntactickitsune.furblorb.io.Decoder;
import net.syntactickitsune.furblorb.io.Encoder;
import net.syntactickitsune.furblorb.io.INamedEnum;

@RegisterSerializable(value = "ScenePatchAddNodes", since = 21)
public final class AddNodePatch extends ScenePatch {

	public String target = "";
	public InjectMode mode = InjectMode.AFTER_TARGET;

	public AddNodePatch() {}

	public AddNodePatch(Decoder in) {
		target = in.readString("TargetNode");
		mode = in.readEnum("Mode", InjectMode.class);
	}

	@Override
	public void write(Encoder to) {
		to.writeString("TargetNode", target);
		to.writeEnum("Mode", mode);
	}

	@Override
	public Set<Properties> getAdditionalProperties() {
		return EnumSet.of(Properties.CHILDREN);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof AddNodePatch a)) return false;
		return Objects.equals(mode, a.mode) && Objects.equals(target, a.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(target, mode);
	}

	public static LegacyAddPatch readLegacyPatch(Decoder in) {
		final AddNodePatch ret = new AddNodePatch();

		ret.mode = in.readEnum("InjectMode", InjectMode.class, InjectMode::legacyId);
		final UUID targetScene = in.readUUID("InjectTargetScene");
		ret.target = in.readString("InjectTargetNode");

		return new LegacyAddPatch(ret, targetScene);
	}

	public static void writeLegacyPatch(Encoder to, AddNodePatch patch, UUID targetScene) {
		to.writeEnum("InjectMode", patch.mode, InjectMode::legacyId);
		to.writeUUID("InjectTargetScene", targetScene);
		to.writeString("InjectTargetNode", patch.target);
	}

	/**
	 * Represents the different ways a patch may be performed.
	 * Specifically, this outlines different injection points a patch may make use of.
	 */
	public static enum InjectMode implements INamedEnum {

		/**
		 * Insert immediately before the target.
		 */
		BEFORE_TARGET("BeforeTarget", "BeforeTarget"), // @Inject(at = @At("HEAD"))

		/**
		 * Insert immediately after the target.
		 */
		AFTER_TARGET("AfterTarget", "AfterTarget"), // @Inject(at = @At("TAIL"))

		/**
		 * Insert inside the target, at the top.
		 */
		INSIDE_AT_START("InsideAtStart", "InsideTargetFirst"), // @Inject(at = @At(value = "INVOKE", ...))

		/**
		 * Insert inside the target, at the end.
		 */
		INSIDE_AT_END("InsideAtEnd", "InsideTargetLast"), // @Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, ...))

		@RequiresFormatVersion(21)
		INSIDE_AT_RANDOM(null, "InsideTargetRandom"); // @Inject(at = @A— hey wait a second, I don't think Mixin has *this* mode of operation.

		private final String legacyId;
		private final String id;

		private InjectMode(String legacyId, String id) {
			this.legacyId = legacyId;
			this.id = id;
		}

		public String legacyId() {
			if (legacyId == null) throw new UnsupportedOperationException("Injection mode " + id + " is not available in this format version");
			return legacyId;
		}

		@Override
		public String id() {
			return id;
		}

		@Override
		public byte formatVersion() {
			return this == INSIDE_AT_RANDOM ? (byte) 21 : 0;
		}
	}

	public static record LegacyAddPatch(AddNodePatch patch, UUID targetScene) {
		
	}
}