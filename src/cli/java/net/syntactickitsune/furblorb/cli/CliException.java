package net.syntactickitsune.furblorb.cli;

/**
 * Represents a kind of exception for simple errors that should be reported to the user without a stacktrace.
 * @author SyntacticKitsune
 */
public final class CliException extends RuntimeException {

	public CliException(String message) {
		super(message);
	}

	public String logMessage() {
		return "! Error: " + getMessage() + ".";
	}
}