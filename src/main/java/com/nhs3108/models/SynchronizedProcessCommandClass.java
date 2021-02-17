package com.nhs3108.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SynchronizedProcessCommandClass {

	public static synchronized void processCommand(String command) {
		try {
			Process process = Runtime.getRuntime().exec(
					command);
			
			  StringBuilder output = new StringBuilder();
			  
			  BufferedReader reader = new BufferedReader( new
			  InputStreamReader(process.getInputStream()));
			  
			  String line; while ((line = reader.readLine()) != null) { output.append(line
			  + "\n"); }
			  
			  int exitVal = process.waitFor(); if (exitVal == 0) {
			  System.out.println("Success!"); System.out.println(output); }
			  else { //abnormal... 
				  
			  }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
