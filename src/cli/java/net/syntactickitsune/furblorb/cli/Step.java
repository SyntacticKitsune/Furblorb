package net.syntactickitsune.furblorb.cli;

/**
 * Represents a single step to perform as part of the CLI's execution.
 * @author SyntacticKitsune
 */
interface Step {

	/**
	 * Performs the step.
	 * @param data The step context.
	 * @throws Exception If literally anything goes wrong. We don't need try-catches where we're going.
	 */
	public void run(WorkingData data) throws Exception;
}