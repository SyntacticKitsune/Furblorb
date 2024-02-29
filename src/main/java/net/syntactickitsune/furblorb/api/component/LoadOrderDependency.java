package net.syntactickitsune.furblorb.api.component;

import java.util.UUID;

import net.syntactickitsune.furblorb.api.io.Decoder;
import net.syntactickitsune.furblorb.api.io.Encoder;
import net.syntactickitsune.furblorb.api.io.INamedEnum;

/**
 * Represents a load order dependency on a single script.
 * @param targetAsset The asset ID of the script the dependency is for.
 * @param relation The dependency relation.
 */
public record LoadOrderDependency(UUID targetAsset, Relation relation) {

	/**
	 * Constructs a {@code LoadOrderDependency} with the specified values.
	 * @param targetAsset The asset ID of the script the dependency is for.
	 * @param relation The dependency relation.
	 */
	public LoadOrderDependency {}

	/**
	 * Decodes a {@code LoadOrderDependency} from the specified {@code Decoder}.
	 * @param in The {@code Decoder}.
	 * @throws NullPointerException If {@code in} is {@code null}.
	 */
	public LoadOrderDependency(Decoder in) {
		this(in.readUUID("TargetAsset"), in.readEnum("Relation", Relation.class));
	}

	/**
	 * Writes this {@code LoadOrderDependency} to the specified {@code Encoder}.
	 * @param to The {@code Encoder}.
	 * @throws NullPointerException If {@code to} is {@code null}.
	 */
	public void write(Encoder to) {
		to.writeUUID("TargetAsset", targetAsset);
		to.writeEnum("Relation", relation);
	}

	/**
	 * Determines the relation between the two scripts in a {@link LoadOrderDependency}.
	 */
	public static enum Relation implements INamedEnum {

		/**
		 * This script should load before the specified script.
		 */
		BEFORE("Before"),

		/**
		 * This script should load after the specified script.
		 */
		AFTER("After");

		private final String id;

		private Relation(String id) {
			this.id = id;
		}

		@Override
		public String id() {
			return id;
		}
	}
}