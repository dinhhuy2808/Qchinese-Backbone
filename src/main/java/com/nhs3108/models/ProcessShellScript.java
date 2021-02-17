package com.nhs3108.models;

public class ProcessShellScript extends Thread {
	
	private static String command;
	
	
	public ProcessShellScript(String command) {
		this.command = command;
	}

	@Override
	public void run() {
		SynchronizedProcessCommandClass.processCommand(command);
	}
}
