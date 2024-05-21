package net.syntactickitsune.furblorb.finmer.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Indicates that an {@link IFurballSerializable} class should be an registered to the {@linkplain FurballSerializables serializable manager}.
 * </p>
 * <p>
 * {@code RegisterSerializable} does not register these itself -- instead, {@link FurballSerializables} reads the file {@code serializables.txt}
 * in the Furblorb jar file to determine which classes to register.
 * This avoids the need to perform expensive annotation scanning at runtime.
 * </p>
 * @author SyntacticKitsune
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterSerializable {

	/**
	 * Specifies the C# name of the class.
	 * This is required because the ID of an {@code IFurballSerializable} depends on the hash of its C# class name.
	 * @return The C# name of this class.
	 */
	public String value();

	/**
	 * Specifies the format version that this class was initially introduced in.
	 * It is an error to read or write this class in older format versions.
	 * @return The format version this class was introduced in.
	 * @see #until()
	 */
	public int since() default 0;

	/**
	 * Specifies the format version that this class was removed in.
	 * It is an error to read or write this class in newer format versions.
	 * The special value 0 means the class has not been removed.
	 * @return The format version this class was removed in.
	 * @see #since()
	 */
	public int until() default 0;
}