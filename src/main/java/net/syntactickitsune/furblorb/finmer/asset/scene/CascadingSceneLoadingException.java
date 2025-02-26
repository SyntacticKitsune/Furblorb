package net.syntactickitsune.furblorb.finmer.asset.scene;

import java.util.ArrayList;
import java.util.List;

import net.syntactickitsune.furblorb.io.FurblorbException;

/**
 * A wrapper for exceptions thrown when deserializing {@link SceneNode SceneNodes} that tracks the path to the problematic node.
 * This makes it easier to diagnose scene deserialization errors.
 * @author SyntacticKitsune
 */
public final class CascadingSceneLoadingException extends FurblorbException {

	final List<String> path = new ArrayList<>();

	/**
	 * Constructs a {@code CascadingException} with the specified values.
	 * @param mostRecent The name of the scene node that is catching the exception.
	 * @param cause The exception in question.
	 */
	public CascadingSceneLoadingException(String mostRecent, Throwable cause) {
		super(cause);
		path.add(mostRecent);
	}

	public void addPath(String path) {
		this.path.add(0, path);
	}

	@Override
	public String getMessage() {
		return "Exception reading " + String.join("→", path);
	}
}