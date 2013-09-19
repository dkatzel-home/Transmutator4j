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

import org.junit.Test;
import static org.junit.Assert.*;
public class TestJavaProcessBuilder {
	private String main = "a.java.main.class";
	private String[] args = new String[]{"arg1","arg2"};
	private File workingDir = new File("aDir");
	

	@Test
	public void defaultClasspathAndCurrentWorkingDir(){
		JavaProcessBuilder sut= new JavaProcessBuilder(main, args);
		ProcessBuilder builder =sut.getBuilder();
		List<String> expectedCommand = new ArrayList<String>();
		expectedCommand.add("java");
		expectedCommand.add("-cp");
		expectedCommand.add(System.getProperty("java.class.path"));
		expectedCommand.add(main);
		for(int i=0; i<args.length; i++){
			expectedCommand.add(args[i]);
		}
		assertEquals(builder.command(),expectedCommand);
		assertEquals(builder.directory(),null);
	}
	@Test
	public void defaultClasspathAndDifferentWorkingDir(){
		JavaProcessBuilder sut= new JavaProcessBuilder(workingDir,main, args);
		ProcessBuilder builder =sut.getBuilder();
		List<String> expectedCommand = new ArrayList<String>();
		expectedCommand.add("java");
		expectedCommand.add("-cp");
		expectedCommand.add(System.getProperty("java.class.path"));
		expectedCommand.add(main);
		for(int i=0; i<args.length; i++){
			expectedCommand.add(args[i]);
		}
		assertEquals(builder.command(),expectedCommand);
		assertEquals(builder.directory(),workingDir);
	}
	@Test
	public void fullConstructor(){
		String classpath ="aClasspath:otherClass.jar";
		JavaProcessBuilder sut= new JavaProcessBuilder(workingDir,main,classpath, args);
		ProcessBuilder builder =sut.getBuilder();
		List<String> expectedCommand = new ArrayList<String>();
		expectedCommand.add("java");
		expectedCommand.add("-cp");
		expectedCommand.add(classpath);
		expectedCommand.add(main);
		for(int i=0; i<args.length; i++){
			expectedCommand.add(args[i]);
		}
		assertEquals(builder.command(),expectedCommand);
		assertEquals(builder.directory(),workingDir);
	}
	
}
