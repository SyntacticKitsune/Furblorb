package net.syntactickitsune.furblorb.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to fields to indicate that they require a specific furball format version range.
 * When serializing something that contains a field annotated with {@code @RequiresFormatVersion},
 * the field will only be serialized if the format version is in the range described by this annotation.
 * The same applies to deserialization.
 * @author SyntacticKitsune
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequiresFormatVersion {

	/**
	 * The minimum format version in which this field is available.
	 * Called "value" here to leverage Java's shorthand annotation writing,
	 * since most fields will not be mysteriously vanishing in future format versions.
	 * @return The minimum format version.
	 */
	public byte value();

	/**
	 * The maximum format version in which this field is available.
	 * So far no fields have ominously disappeared, but there's always a possibility.
	 * Thus, this is here.
	 * @return The maximum format version.
	 */
	public byte max() default Byte.MAX_VALUE;
}