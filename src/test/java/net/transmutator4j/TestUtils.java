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
package net.transmutator4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Permission;
import java.util.Scanner;

public final class TestUtils {
	
	public static final class TriedToExitException extends SecurityException{

		private static final long serialVersionUID = 1L;
		private final int exitCode;

		private TriedToExitException(int exitCode) {
			this.exitCode = exitCode;
			
		}

		public int getExitCode() {
			return exitCode;
		}

		@Override
		public String getMessage() {
			return String.format("tried to exit with exit code %d", exitCode);
		}
		
		
		
	}
	
	/**
	 * Security Manager that does not allow {@link System#exit(int)} or 
	 * {@link Runtime#exit(int)} calls to terminate the JVM.
	 * If one of these calls is made, then a {@link TriedToExitException}
	 * is thrown instead.  
	 * <p/>
	 * This is useful for testing paths that call exit
	 * without killing the test harness.
	 */
	public static final SecurityManager UnExitableSecurityManager  = new SecurityManager(){

		@Override
		public void checkPermission(Permission perm) {
			//allow everything
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			//allow everything
		}
		/**
		 * Always throws a {@link TriedToExitException} to prevent
		 * the jvm from exiting
		 * {@inheritDoc}
		 * @throws TriedToExitException always. 
		 * 
		 */
		@Override
		public void checkExit(int status) {
			throw new TriedToExitException(status);
		}
		
	};
	/**
	 * Read the entire given file and return the contents
	 * as one long String.  This should only be used for small
	 * text files and is not intended to be used on large files
	 * or files that are not text only.
	 * @param textFile
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String readTextFileAsString(File textFile)
			throws FileNotFoundException {
		StringBuilder outputText = new StringBuilder();
		Scanner scanner = new Scanner(textFile);
		while(scanner.hasNextLine()){
			outputText.append(scanner.nextLine()).append(String.format("%n"));
		}
		scanner.close();
		String actualText = outputText.toString();
		return actualText;
	}
}
