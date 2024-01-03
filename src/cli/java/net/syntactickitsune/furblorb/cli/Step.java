package net.syntactickitsune.furblorb.cli;

interface Step {

	public void run(WorkingData data) throws Exception;
}