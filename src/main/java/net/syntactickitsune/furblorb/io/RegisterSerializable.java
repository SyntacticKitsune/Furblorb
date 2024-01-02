package net.syntactickitsune.furblorb.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers an {@link IFurballSerializable} {@code class} to the {@linkplain FurballSerializables serializable manager}.
 * @author SyntacticKitsune
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterSerializable {

	/**
	 * @return The C# name of this {@code class}.
	 */
	public String value();

	/**
	 * @return The format version this {@code class} was introduced.
	 */
	public int since() default 0;
}