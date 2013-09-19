/*******************************************************************************
 * Copyright (c) 2010 - 2013 Danny Katzel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package net.transmutator4j.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * JavaProcessBuilder runs a Java main
 * using a Process object.
 * @author dkatzel-home
 *
 */
public class JavaProcessBuilder{
	private final ProcessBuilder builder;
	/**
	 * Creates a Java Process which will run the given mainClass using the 
	 * given arguments from the current working directory using the current class path.
	 * This constructor is the same as running
	 * {@link #JavaProcessBuilder(File, String, String...) 
	 * new JavaProcessBuilder( null,mainClass,args)}
	 * @param mainClass the main class to run
	 * @param args the arguments for the main class
	 * @see #JavaProcessBuilder(File, String, String, String...)
	 */
	public JavaProcessBuilder(String mainClass, String...args){
		this(null, mainClass,args);
	}
	/**
	 * Creates a Java Process which will run the given mainClass using the 
	 * given arguments from the current working directory using the current class path.
	 * This constructor is the same as running
	 * {@link #JavaProcessBuilder(File, String, String, String...) 
	 * new JavaProcessBuilder( workingDir,mainClass,System.getProperty("java.class.path"),args)}
	 * @param workingDir working directory to run the process from (setting this to null means current working dir)
	 * @param mainClass the main class to run
	 * @param args the arguments for the main class
	 * @see #JavaProcessBuilder(File, String, String, String...)
	 */
	public JavaProcessBuilder(File workingDir,String mainClass, String...args){
		this(workingDir, mainClass,System.getProperty("java.class.path"),args);
	}
	/**
	 * Creates a Java Process which will run the given mainClass using the 
	 * given arguments from the given working directory.
	 * @param workingDir working directory to run the process from (setting this to null means current working dir)
	 * @param mainClass the main class to run
	 * @param classpath the classpath of java program to run.
	 * @param args the arguments for the main class
	 * 
	 */
	public JavaProcessBuilder(File workingDir, String mainClass, String classpath,String...args){
		
		List<String> commandLine = new ArrayList<String>();
		commandLine.add("java");
		commandLine.add("-cp");
		commandLine.add(classpath);
		commandLine.add(mainClass);
		for(int i=0; i<args.length; i++){
			commandLine.add(args[i]);
		}
		this.builder = new ProcessBuilder(commandLine.toArray(new String[]{}));
		this.builder.directory(workingDir);
		
	}
	
	
	/**
	 * @return the builder
	 */
	public ProcessBuilder getBuilder() {
		return builder;
	}
	
	
	
}
