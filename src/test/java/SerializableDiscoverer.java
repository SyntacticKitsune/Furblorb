import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import net.syntactickitsune.furblorb.io.IFurballSerializable;
import net.syntactickitsune.furblorb.io.RegisterSerializable;

final class SerializableDiscoverer {

	public static void main(String[] args) {
		final Reflections refs = new Reflections("net.syntactickitsune.furblorb.api");

		final Set<Class<?>> serializables = refs.get(Scanners.SubTypes
				.of(IFurballSerializable.class).asClass());
		final Set<Class<?>> registeredSerializables = refs.get(Scanners.SubTypes
				.of(Scanners.TypesAnnotated.with(RegisterSerializable.class))
				.asClass());

		final List<String> registered = new ArrayList<>();
		final List<String> unregistered = new ArrayList<>();

		for (Class cls : serializables) {
			if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) continue;
			(registeredSerializables.contains(cls) ? registered : unregistered).add(cls.getName().replace('/', '.')); // Most sane one-liner.
		}

		Collections.sort(registered);
		Collections.sort(unregistered);

		System.out.println("Registered:\n" + String.join("\n", registered));

		System.out.println("\n\nUnregistered:\n" + String.join("\n", unregistered));
	}
}